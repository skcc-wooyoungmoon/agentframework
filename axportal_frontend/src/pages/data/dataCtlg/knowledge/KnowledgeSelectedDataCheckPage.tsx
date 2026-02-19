import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIGroup, UIFormField,  } from '@/components/UI/molecules';
import { UIDataCnt, UIFileBox } from '@/components/UI/atoms';

type KnowledgeSelectedDataCheckPageProps = {
  selectedItems: any[];
  setSelectedItems: (value: any[]) => void;
  selectedItemsMap: Map<string, any>;
  setSelectedItemsMap: (value: Map<string, any>) => void;
};

export const KnowledgeSelectedDataCheckPage: React.FC<KnowledgeSelectedDataCheckPageProps> = ({ 
    selectedItems, 
    setSelectedItems, 
    selectedItemsMap, 
    setSelectedItemsMap 
}) => {  

// 선택된 항목 삭제 핸들러
  const handleRemoveItem = (itemId: string) => {
    const newMap = new Map(selectedItemsMap);
    newMap.delete(itemId);
    setSelectedItemsMap(newMap);
    setSelectedItems(Array.from(newMap.values()));
  };


return (
    <>
        <UIArticle>
            <UIFormField gap={8} direction='column'>
                <UIDataCnt count={selectedItems.length} prefix='선택된 데이터 총' />
                <UIGroup gap={16} direction='column'>
                <div>
                    {selectedItems.length > 0 && (
                    <div className='space-y-3'>
                        {selectedItems.map((item: any) => (
                        <UIFileBox
                            key={item.id}
                            variant='default'
                            size='full'
                            fileName={item.datasetCardName || item.name}
                            onFileRemove={() => handleRemoveItem(item.id)}
                            className='w-full'
                        />
                        ))}
                    </div>
                    )}
                </div>
                </UIGroup>
            </UIFormField>
        </UIArticle>
    </>
  );
}