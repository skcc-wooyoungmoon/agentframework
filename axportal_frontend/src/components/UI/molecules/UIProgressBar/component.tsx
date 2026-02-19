import { UITypography } from '@/components/UI/atoms';

export interface UIProgressBarProps {
  label: string;
  value: number;
  total?: number;
  color?: string;
  textColor?: string;
  showValue?: boolean;
  height?: number;
}

export const UIProgressBar: React.FC<UIProgressBarProps> = ({ label, value, total = 100, color = '#2670FF', textColor, showValue = true, height = 12 }) => {
  const percentage = Math.min((value / total) * 100, 100);
  const valueColor = textColor || color;

  return (
    <div className='w-full'>
      {/* Label */}
      <div className='mb-1'>
        <UITypography variant='body-1' className='text-[#373E4D]'>
          {label}
        </UITypography>
      </div>

      {/* Progress Bar with Value */}
      <div className='flex items-center gap-3'>
        <div className='relative flex-1 rounded-full' style={{ height: `${height}px` }}>
          <div
            className='absolute top-0 left-0 h-full rounded-full transition-all duration-300'
            style={{
              width: `${percentage}%`,
              backgroundColor: color,
            }}
          />
        </div>
        {showValue && (
          <UITypography variant='title-4' className='font-semibold' style={{ color: valueColor }}>
            {value.toLocaleString()}건
          </UITypography>
        )}
      </div>
    </div>
  );
};
