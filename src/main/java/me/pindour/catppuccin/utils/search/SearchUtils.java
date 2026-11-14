package me.pindour.catppuccin.utils.search;

import me.pindour.catppuccin.utils.search.results.ModuleSearchResult;
import me.pindour.catppuccin.utils.search.results.SettingSearchResult;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.Utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SearchUtils {
    /**
     * Performs a global search across all available searchable objects (modules, settings...) based on the provided query.
     * <p>
     * Best matches (lowest score) are placed at the beginning of the list.
     *
     * @param query The text to search for.
     * @return A combined and sorted list of {@link SearchResult}.
     */
    public static List<SearchResult> search(String query) {
        int limit = Math.max(1, Config.get().moduleSearchCount.get());

        List<SearchResult> results = new ArrayList<>();

        results.addAll(searchModules(query));
        results.addAll(searchSettings(query));

        results.sort(Comparator.comparingInt(SearchResult::score));

        return results.size() > limit * 2 ? results.subList(0, limit * 2) : results;
    }

    /**
     * Searches for modules matching the query by their title or aliases.
     * <p>
     * Best matches (lowest score) are placed at the beginning of the list.
     *
     * @param query The text to search for.
     * @return A list of {@link ModuleSearchResult} objects matching the query.
     */
    public static List<ModuleSearchResult> searchModules(String query) {
        List<ModuleSearchResult> results = new ArrayList<>();

        for (Module module : Modules.get().getAll()) {
            int score = Integer.MAX_VALUE;
            String matchedAlias = null;

            if (Utils.searchTextDefault(module.title, query, false)) {
                score = Utils.searchLevenshteinDefault(module.title, query, false);
            }

            if (Config.get().moduleAliases.get()) {
                for (String alias : module.aliases) {
                    if (!Utils.searchTextDefault(alias, query, false)) continue;

                    int aliasScore = Utils.searchLevenshteinDefault(alias, query, false);
                    if (aliasScore < score) {
                        score = aliasScore;
                        matchedAlias = Utils.nameToTitle(alias);
                    }
                }
            }

            if (score != Integer.MAX_VALUE) {
                results.add(new ModuleSearchResult(module, matchedAlias, score));
            }
        }

        results.sort(Comparator.comparingInt(SearchResult::score));
        return results;
    }

    /**
     * Searches for individual settings within all registered modules.
     * <p>
     * Best matches (lowest score) are placed at the beginning of the list.
     *
     * @param query The text to search for.
     * @return A list of {@link SettingSearchResult} objects matching the query.
     */
    public static List<SettingSearchResult> searchSettings(String query) {
        List<SettingSearchResult> results = new ArrayList<>();

        for (Module module : Modules.get().getAll()) {
            for (SettingGroup sg : module.settings) {
                for (Setting<?> setting : sg) {
                    if (setting.title == null || setting.title.isEmpty()) continue;
                    if (!Utils.searchTextDefault(setting.title, query, false)) continue;

                    int score = Utils.searchLevenshteinDefault(setting.title, query, false);
                    results.add(new SettingSearchResult(setting, sg, score));
                }
            }
        }

        results.sort(Comparator.comparingInt(SearchResult::score));
        return results;
    }
}