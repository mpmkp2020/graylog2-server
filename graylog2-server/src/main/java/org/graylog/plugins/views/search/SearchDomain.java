/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog.plugins.views.search;

import org.graylog.plugins.views.search.db.SearchDbService;
import org.graylog.plugins.views.search.errors.PermissionException;
import org.graylog.plugins.views.search.views.ViewDTO;
import org.graylog.plugins.views.search.views.ViewService;
import org.graylog2.plugin.database.users.User;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SearchDomain {
    private final SearchDbService dbService;
    private final ViewService viewService;

    @Inject
    public SearchDomain(SearchDbService dbService, ViewService viewService) {
        this.dbService = dbService;
        this.viewService = viewService;
    }

    public Optional<Search> getForUser(String id, User user, Predicate<ViewDTO> viewReadPermission) {
        final Optional<Search> search = dbService.get(id);

        search.ifPresent(s -> checkPermission(user, viewReadPermission, s));

        return search;
    }

    private void checkPermission(User user, Predicate<ViewDTO> viewReadPermission, Search s) {
        if (!hasReadPermissionFor(user, viewReadPermission, s))
            throw new PermissionException("User " + user.getName() + " does not have permission to load search " + s.id());
    }

    public List<Search> getAllForUser(User user, Predicate<ViewDTO> viewReadPermission) {
        return dbService.streamAll()
                .filter(s -> hasReadPermissionFor(user, viewReadPermission, s))
                .collect(Collectors.toList());
    }

    private boolean hasReadPermissionFor(User user, Predicate<ViewDTO> viewReadPermission, Search search) {
        if (isOwned(search, user)) {
            return true;
        }
        // Allowed if permissions exist for a referencing view
        final Collection<ViewDTO> views = viewService.forSearch(search.id());
        if (views.isEmpty())
            return false;

        return views.stream().anyMatch(viewReadPermission);
    }

    private boolean isOwned(Search search, User user) {
        return search.owner().map(o -> o.equals(user.getName())).orElse(false);
    }
}
