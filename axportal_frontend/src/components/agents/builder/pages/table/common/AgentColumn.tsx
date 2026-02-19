
import { type KeyTableData } from '@/components/agents/builder/types/Agents';
import { type CustomColumn } from '@/components/agents/builder/types/table.ts';

import { DataCell } from '@/components/agents/builder/pages/table/cells/DataCell.tsx';
import { NodeTypeCell } from '@/components/agents/builder/pages/table/cells/NodeTypeCell.tsx';

const createKeyTableColumns = (_selectedId?: string | null, _onSelect?: (id: string) => void): ReadonlyArray<CustomColumn<KeyTableData>> => {
  return [
    {
      id: 'key',
      accessor: 'key',
      Header: 'Key',
      Cell: ({ row }) => <DataCell data={row} field='key' className='table-cell-wrap' />,
      sortable: false,
    },
    {
      id: 'nodeType',
      accessor: 'nodeType',
      Header: '노드 타입',
      Cell: ({ row }) => <NodeTypeCell nodeType={row.nodeType} />,
      sortable: false,
    },
    {
      id: 'nodeName',
      accessor: 'nodeName',
      Header: '노드 이름',
      Cell: ({ row }) => <DataCell data={row} field='nodeName' className='table-cell-wrap' />,
      sortable: false,
    },
  ];
};

const keyTableColumnsConfig = [
  { key: 'key', value: '150px' },
  { key: 'nodeType', value: '160px' },
  { key: 'nodeName', value: '200px' },
];

export { createKeyTableColumns, keyTableColumnsConfig };
