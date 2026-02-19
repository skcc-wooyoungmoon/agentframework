# Serve the application with Nginx, assuming the 'dist' directory is pre-built
FROM public.ecr.aws/nginx/nginx:1.23.3
RUN chgrp -R 1000790001 /var/cache/nginx /var/run /var/log/nginx && \
    chmod -R 770 /var/cache/nginx /var/run /var/log/nginx && \
    chown -R 1000790001 /run

# nginx 설정 파일 복사
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 빌드된 파일 복사
COPY dist/ /usr/share/nginx/html

EXPOSE 8080
CMD ["nginx", "-g", "daemon off;"]