/**
 * 在生产环境 代理是无法生效的，所以这里没有生产环境的配置
 * The agent cannot take effect in the production environment
 * so there is no configuration of the production environment
 * For details, please see
 * https://pro.ant.design/docs/deploy
 */
export default {
  dev: {
    '/admin/apis': {
      target: 'http://localhost:8080/',
      changeOrigin: true,
      logLevel: 'debug',
      onProxyRes(proxyRes: any) {
        const key = 'set-cookie';
        if (proxyRes.headers[key]) {
          const cookies = proxyRes.headers[key].join('').split(' ');
          proxyRes.headers[key] = [cookies[0], 'Path=/'].join(' ');
        }
      },
    },
    '/admin/login': {
      target: 'http://localhost:8080/',
      changeOrigin: true,
      logLevel: 'debug',
      onProxyRes(proxyRes: any) {
        const key = 'set-cookie';
        if (proxyRes.headers[key]) {
          const cookies = proxyRes.headers[key].join('').split(' ');
          proxyRes.headers[key] = [cookies[0], 'Path=/'].join(' ');
        }
      },
    },
  },
  test: {},
  pre: {},
};
