import type { UICardInfoItemProps } from "./types";

export const UICardInfoItem: React.FC<UICardInfoItemProps> = ({
  data,
  className = "",
  ...props
}) => {
  return (
    <div className={"card-info-item " + className} {...props}>
      {data.map((item, index) => (
        <div key={index} className="colum">
          <span className="label">{item.label}</span>
          <span className="value">{item.value}</span>
        </div>
      ))}
    </div>
  );
};