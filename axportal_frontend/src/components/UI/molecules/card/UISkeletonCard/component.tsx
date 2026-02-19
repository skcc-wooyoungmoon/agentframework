export function UISkeletonCard() {
  return (
    <div className='grid-card'>
      <div className='grid-card-header mb-3'>
        {/* 체크박스 영역 주석
        <div className='grid-card-checkbox'>
          <div className='skeleton skeleton-rect w-[20px] h-[20px]' />
        </div> */}
        <div className='grid-card-title pb-3 flex flex-col flex-1 gap-2'>
          <div className='skeleton skeleton-rect skeleton-animate w-[60%] h-[24px] mb-2' />
          <div className='skeleton skeleton-rect skeleton-animate w-full h-[24px]' />
        </div>
        {/* 더보기 버튼 주석
        <div>
          <div className='skeleton skeleton-circle w-[24px] h-[24px]' />
        </div> */}
      </div>

      <div className='flex flex-col gap-2'>
        {/* 컬럼 주석
        <div className='grid-card-status pb-3'>
          <div className='skeleton skeleton-rect skeleton-animate w-[80px] h-[24px]' />
        </div> */}
        {[1, 2, 3, 4].map(index => (
          <div key={index} className='flex'>
            <div className='skeleton skeleton-rect skeleton-animate w-full h-[24px]' />
          </div>
        ))}
      </div>
    </div>
  );
}
