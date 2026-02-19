import type { RouteType } from '@/routes/types';

/**
 * 라우트 경로 매칭 전략
 * 1. 정확한 경로 매칭
 * 2. 동적 라우트 매칭 (예: :id, :userId/projects/:projectId)
 */
function matchRoute(route: RouteType, targetPath: string): boolean {
  if (!route.path) {
    return false;
  }

  // 정확한 경로 매칭
  if (route.path === targetPath) {
    return true;
  }

  // 동적 라우트 매칭 (예: :id, :userId/projects/:projectId)
  // 세그먼트 수가 같아야 하고, 각 세그먼트가 매칭되어야 함
  const routePathSegments = route.path.split('/').filter(Boolean);
  const targetPathSegments = targetPath.split('/').filter(Boolean);

  // 세그먼트 수가 다르면 매칭 실패
  if (routePathSegments.length !== targetPathSegments.length) {
    return false;
  }

  // 각 세그먼트를 비교
  return routePathSegments.every((routeSegment, i) => {
    const targetSegment = targetPathSegments[i];
    // 동적 세그먼트(:로 시작)는 모든 값과 매칭
    if (routeSegment.startsWith(':')) {
      return true;
    }
    // 정적 세그먼트는 정확히 일치해야 함
    return routeSegment === targetSegment;
  });
}

/**
 * 재귀적으로 라우트를 찾는 함수
 * 정적 경로를 동적 경로보다 우선적으로 찾음
 * @param routes - 검색할 라우트 배열
 * @param targetPath - 찾을 경로
 * @returns 매칭된 라우트 또는 null
 */
function findRouteByPath(routes: RouteType[], targetPath: string): RouteType | null {
  // 먼저 정적 경로를 찾기
  for (const route of routes) {
    if (isStaticRoute(route) && matchRoute(route, targetPath)) {
      return route;
    }
  }

  // 정적 경로가 없으면 동적 경로 찾기
  for (const route of routes) {
    if (!isStaticRoute(route) && matchRoute(route, targetPath)) {
      return route;
    }
  }

  // 현재 depth에서 직접 매칭 실패 시 자식 라우트에서 재귀 검색
  for (const route of routes) {
    if (route.children) {
      const found = findRouteByPath(route.children, targetPath);
      if (found) {
        return found;
      }
    }
  }

  return null;
}

/**
 * 라우트가 정적 경로인지 확인 (동적 세그먼트가 없는지)
 */
function isStaticRoute(route: RouteType): boolean {
  return route.path ? !route.path.includes(':') : false;
}

/**
 * 경로 세그먼트를 순회하며 breadcrumb을 생성하는 재귀 함수
 * 여러 세그먼트를 포함하는 라우트도 처리 (예: ':userId/projects/:projectId')
 * depth를 하나씩 더 들어가서 최대한 찾으려고 시도
 * 긴 경로부터 시도하고, 정적 경로를 동적 경로보다 우선 매칭
 * @param routes - 현재 검색할 라우트 배열
 * @param pathSegments - 남은 경로 세그먼트 배열
 * @param breadcrumb - 현재까지 생성된 breadcrumb 배열
 */
function buildBreadcrumbRecursive(routes: RouteType[], pathSegments: string[], breadcrumb: string[]): void {
  if (pathSegments.length === 0 || routes.length === 0) {
    return;
  }

  let matchedRoute: RouteType | undefined;
  let usedSegmentCount = 0;

  // 여러 세그먼트 조합을 시도 (긴 경로부터 시도하여 더 구체적인 라우트 우선 매칭)
  // 예: ['user123', 'projects', 'proj456']에서
  // 1. 'user123/projects/proj456'로 매칭 시도 (가장 긴 경로)
  // 2. 'user123/projects'로 매칭 시도
  // 3. 'user123'만으로 매칭 시도
  const maxSegments = Math.min(pathSegments.length, 5);

  // 긴 경로부터 역순으로 시도
  for (let segmentCount = maxSegments; segmentCount >= 1; segmentCount--) {
    const combinedPath = pathSegments.slice(0, segmentCount).join('/');

    // 정적 경로를 먼저 찾기 (동적 경로보다 우선)
    const staticRoute = routes.find(route => {
      if (!route.path) return false;
      return isStaticRoute(route) && matchRoute(route, combinedPath);
    });

    if (staticRoute) {
      matchedRoute = staticRoute;
      usedSegmentCount = segmentCount;
      break;
    }

    // 정적 경로가 없으면 동적 경로 찾기
    const dynamicRoute = routes.find(route => {
      if (!route.path) return false;
      return !isStaticRoute(route) && matchRoute(route, combinedPath);
    });

    if (dynamicRoute) {
      matchedRoute = dynamicRoute;
      usedSegmentCount = segmentCount;
      break;
    }

    // 현재 depth에서 직접 매칭 실패 시 재귀 검색 (자식 라우트까지 깊이 들어가서 찾기)
    // 재귀 검색도 정적 경로를 우선으로 찾도록 수정 필요
    const foundRoute = findRouteByPath(routes, combinedPath);
    if (foundRoute) {
      matchedRoute = foundRoute;
      usedSegmentCount = segmentCount;
      break;
    }
  }

  // 매칭된 라우트가 있으면 breadcrumb에 추가하고 다음 depth로 진행
  if (matchedRoute && usedSegmentCount > 0) {
    breadcrumb.push(matchedRoute.label);

    // 사용한 세그먼트를 제거하고 남은 세그먼트로 재귀 호출
    const remainingSegments = pathSegments.slice(usedSegmentCount);
    if (remainingSegments.length > 0 && matchedRoute.children) {
      buildBreadcrumbRecursive(matchedRoute.children, remainingSegments, breadcrumb);
    }
  }
  // 매칭 실패 시 그냥 중단 (더 이상 찾지 않음)
}

/**
 * 현재 경로를 기반으로 breadcrumb 경로를 생성하는 함수
 * @param currentPath - 현재 경로 (예: '/model/modelCtlg')
 * @param routeConfig - 전체 라우트 설정
 * @returns breadcrumb 경로 배열 (예: ['모델', '모델 카탈로그'])
 */
export function generateBreadcrumb(currentPath: string, routeConfig: RouteType[]): string[] {
  const pathSegments = currentPath.split('/').filter(Boolean);
  const breadcrumb: string[] = [];

  // PROTECTED 그룹에서 메인 라우트들을 찾기
  const protectedGroup = routeConfig.find(route => route.id === 'PROTECTED');
  if (!protectedGroup?.children) {
    return breadcrumb;
  }

  // 첫 번째 세그먼트로 1depth 메뉴 찾기
  const firstSegment = pathSegments[0];
  const firstLevelRoute = protectedGroup.children.find(route => route.path === firstSegment);

  if (!firstLevelRoute) {
    return breadcrumb;
  }

  // 1depth 메뉴 라벨 추가
  breadcrumb.push(firstLevelRoute.label);

  // 나머지 경로들을 재귀적으로 처리
  if (pathSegments.length > 1 && firstLevelRoute.children) {
    const remainingSegments = pathSegments.slice(1);
    buildBreadcrumbRecursive(firstLevelRoute.children, remainingSegments, breadcrumb);
  }

  return breadcrumb;
}
