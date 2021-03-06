// @flow strict
import * as React from 'react';
import { useEffect, useState } from 'react';
import * as Immutable from 'immutable';

import PaginatedList, { INITIAL_PAGE } from 'components/common/PaginatedList';
import SearchForm from 'components/common/SearchForm';
import EmptyResult from 'components/common/EmptyResult';

import PaginatedItem from './PaginatedItem';

export type PaginationInfo = {
  total: number,
  count: number,
  page: number,
  perPage: number,
  query: string,
};

export type DescriptiveItem = {
  +id: string,
  +name: string,
  +description: string,
};

type ListOfDescriptiveItems = Immutable.List<DescriptiveItem>;

export type PaginatedListType = {
  pagination: PaginationInfo,
  list: ListOfDescriptiveItems,
};

type Props = {
  noDataText?: string,
  onLoad: (paginationInfo: PaginationInfo, isSubscribed: boolean) => Promise<?PaginatedListType>,
  overrideList?: PaginatedListType,
  onDeleteItem?: (DescriptiveItem) => void,
  queryHelper?: React.Node,
};

const pageSizes = [5, 10, 30];
export const defaultPageInfo = { page: INITIAL_PAGE, perPage: pageSizes[0], query: '', total: 0, count: 0 };

const PaginatedItemOverview = ({ onLoad, overrideList, onDeleteItem, queryHelper, noDataText }: Props) => {
  const [items, setItems] = useState();
  const [paginationInfo, setPaginationInfo] = useState(defaultPageInfo);

  const _setResponse = (response: ?PaginatedListType) => {
    if (!response) {
      return;
    }

    const { list, pagination } = response;
    setPaginationInfo(pagination);
    setItems(list);
  };

  useEffect(() => _setResponse(overrideList), [overrideList]);

  useEffect(() => {
    let isSubscribed = true;

    onLoad(paginationInfo, isSubscribed).then((response) => {
      if (isSubscribed) {
        _setResponse(response);
      }
    });

    return () => { isSubscribed = false; };
  }, []);

  const _onPageChange = (page, perPage) => {
    const pageInfo = {
      ...paginationInfo,
      page,
      perPage,
    };
    onLoad(pageInfo, true).then(_setResponse);
  };

  const _onSearch = (query) => {
    const pageInfo = {
      ...paginationInfo,
      page: INITIAL_PAGE,
      query,
    };
    onLoad(pageInfo, true).then(_setResponse);
  };

  const result = items && items.size > 0
    ? items.toArray().map((item) => <PaginatedItem key={item.id} onDeleteItem={onDeleteItem} item={item} />)
    : <EmptyResult>{noDataText}</EmptyResult>;

  return (
    <PaginatedList onChange={_onPageChange}
                   pageSize={paginationInfo.perPage}
                   totalItems={paginationInfo.total}
                   pageSizes={pageSizes}
                   activePage={paginationInfo.page}>
      <SearchForm onSearch={_onSearch}
                  label="Filter"
                  wrapperClass="has-bm"
                  placeholder="Enter query to filter"
                  queryHelpComponent={queryHelper}
                  searchButtonLabel="Filter" />
      <div>
        {result}
      </div>
    </PaginatedList>
  );
};

PaginatedItemOverview.defaultProps = {
  onDeleteItem: undefined,
  overrideList: undefined,
  noDataText: 'No items found to display.',
  queryHelper: undefined,
};

export default PaginatedItemOverview;
