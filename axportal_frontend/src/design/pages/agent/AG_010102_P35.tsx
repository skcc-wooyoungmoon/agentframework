import { UICode } from '@/components/UI/atoms/UICode';

export const AG_010102_P35 = () => {
  const dummyCode = `def calculate_fibonacci(n):
    """
    Fibonacci 수열을 계산하는 함수입니다.
    이 함수는 동적 프로그래밍을 사용하여 효율적으로 계산합니다.
    """
    if n <= 0:
        return 0
    elif n == 1:
        return 1

    # DP 배열을 초기화합니다
    fib = [0] * (n + 1)
    fib[0] = 0
    fib[1] = 1

    # Fibonacci 수열을 계산합니다
    for i in range(2, n + 1):
        fib[i] = fib[i - 1] + fib[i - 2]

    return fib[n]

def factorial(n):
    """
    팩토리얼 함수입니다.
    재귀를 사용하여 구현했습니다.
    """
    if n <= 1:
        return 1
    return n * factorial(n - 1)

def bubble_sort(arr):
    """
    버블 정렬 알고리즘을 구현한 함수입니다.
    시간복잡도는 O(n^2)입니다.
    """
    n = len(arr)
    for i in range(n):
        for j in range(0, n - i - 1):
            if arr[j] > arr[j + 1]:


            

                # Swap 연산
                arr[j], arr[j + 1] = arr[j + 1], arr[j]
    return arr

def quick_sort(arr):
    """
    퀵 정렬 알고리즘을 구현한 함수입니다.
    평균 시간복잡도는 O(n log n)입니다.
    """
    if len(arr) <= 1:
        return arr

    pivot = arr[len(arr) // 2]
    left = [x for x in arr if x < pivot]
    middle = [x for x in arr if x == pivot]
    right = [x for x in arr if x > pivot]

    return quick_sort(left) + middle + quick_sort(right)
`;

  return (
    <div className='flex h-full overflow-hidden'>
      {/* 소스코드 영역 */}
      <UICode value={dummyCode} language='python' theme='dark' width='100%' readOnly={true} minHeight='450px' maxHeight='450px' />
    </div>
  );
};
