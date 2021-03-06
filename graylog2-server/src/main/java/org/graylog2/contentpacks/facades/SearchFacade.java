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
package org.graylog2.contentpacks.facades;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.graylog.plugins.views.search.db.SearchDbService;
import org.graylog.plugins.views.search.views.ViewDTO;
import org.graylog.plugins.views.search.views.ViewService;
import org.graylog2.contentpacks.model.ModelType;
import org.graylog2.contentpacks.model.ModelTypes;
import org.graylog2.shared.users.UserService;

import javax.inject.Inject;

public class SearchFacade extends ViewFacade {
    public static final ModelType TYPE_V1 = ModelTypes.SEARCH_V1;

    @Inject
    public SearchFacade(ObjectMapper objectMapper, SearchDbService searchDbService, ViewService viewService, UserService userService) {
        super(objectMapper, searchDbService, viewService, userService);
    }

    @Override
    public ModelType getModelType() {
        return TYPE_V1;
    }


    @Override
    public ViewDTO.Type getDTOType() {
        return ViewDTO.Type.SEARCH;
    }
}
