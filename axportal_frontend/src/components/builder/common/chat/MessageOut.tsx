interface MessageOutProps {
  text: string;
  time: string;
}

const MessageOut = ({ text }: MessageOutProps) => {
  return (
    <div className='flex items-end justify-end gap-3.5 px-5'>
      <div className='flex flex-col gap-1.5 max-w-[80%]'>
        <div
          className='rounded-be-none card flex flex-col gap-2.5 bg-primary p-3 text-2sm font-medium text-primary-inverse shadow-none opacity-100 py-3 px-4 rounded-tl-3xl rounded-tr-3xl rounded-br-sm rounded-bl-3xl bg-[#2670FF] text-[#fff] font-semibold text-sm leading-5 tracking-[-0.01%] align-middle break-words'
          style={{
            maxWidth: '100%',
            wordWrap: 'break-word',
            overflowWrap: 'break-word',
            whiteSpace: 'pre-wrap', // 줄바꿈 유지
          }}
        >
          {text}
        </div>
      </div>
    </div>
  );
};

export { MessageOut };