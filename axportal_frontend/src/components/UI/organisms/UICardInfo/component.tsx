import { UICardInfoItem } from "../../molecules";
import { UIIcon2 } from '@/components/UI/atoms/UIIcon2';
import type { UICardInfoProps } from "./types";

export const UICardInfo: React.FC<UICardInfoProps> = ({
  items,
  columns = 2,
  className = "",
  noDataMessage = "조회된 데이터가 없습니다.",
  ...props
}) => {
  const gridStyle = {
    gridTemplateColumns: `repeat(${columns}, 1fr)`,
  };

  if (!items || items.length === 0) {
    return (
      <div className={"card-info-no-data " + className} {...props}>
        <span className="ico-nodata"><UIIcon2 className='ic-system-80-default-nodata' /></span>
        <span className="text-body-1 secondary-neutral-500">{noDataMessage}</span>
      </div>
    );
  }

  return (
    <div
      className={"card-info " + className}
      style={gridStyle}
      {...props}
    >
      {items.map((item, index) => (
        <UICardInfoItem key={index} data={item.data} />
      ))}
    </div>
  );
};