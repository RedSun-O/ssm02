import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.Test;

/**
 * @program: gittest
 * @description:
 * @author: Jian
 * @create: 2021-02-14 16:33
 **/
public class Test01 {
    @Test
    public void test01() throws Exception {
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000,3,3000);
        CuratorFramework client = CuratorFrameworkFactory.newClient("127.0.0.1:2181", 3000, 3000, retryPolicy);
        client.start();
       // client.create().forPath("/api");
        client.create().forPath("/a", "hello world".getBytes());
        client.close();
    }
    @Test
    public void test02() throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 1);
        CuratorFramework client = CuratorFrameworkFactory.newClient("127.0.0.1:2181", 1000, 1000, retryPolicy);
        client.start();
        System.out.println("连接成功");
        final NodeCache nodeCache = new NodeCache(client, "/a");
        nodeCache.start(true);
        System.out.println("初始化"+new String(nodeCache.getCurrentData().getData()));
        nodeCache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                System.out.println("修改后的值:" + new String(nodeCache.getCurrentData().getData()));
            }
        });
        System.in.read();
    }

    @Test
    public void test03() throws Exception {
        //重试策略
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 1);
        //创建客户端
        CuratorFramework client = CuratorFrameworkFactory.newClient("127.0.0.1:2181", 1000, 1000,retryPolicy );
        //启动
        client.start();
        PathChildrenCache pathChildrenCache = new PathChildrenCache(client,"/b",true);
        pathChildrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        System.out.println(pathChildrenCache.getCurrentData());
//添加监听
        pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                if(event.getType() == PathChildrenCacheEvent.Type.CHILD_UPDATED){
                    System.out.println("子节点更新");
                    System.out.println("节点:"+event.getData().getPath());
                    System.out.println("数据" + new String(event.getData().getData()));
                }else if(event.getType() == PathChildrenCacheEvent.Type.INITIALIZED ){
                    System.out.println("初始化操作");
                }else if(event.getType() == PathChildrenCacheEvent.Type.CHILD_REMOVED ){
                    System.out.println("删除子节点");
                    System.out.println("节点:"+event.getData().getPath());
                    System.out.println("数据" + new String(event.getData().getData()));
                }else if(event.getType() == PathChildrenCacheEvent.Type.CHILD_ADDED ){
                    System.out.println("添加子节点");
                    System.out.println("节点:"+event.getData().getPath());
                    System.out.println("数据" + new String(event.getData().getData()));
                }else if(event.getType() == PathChildrenCacheEvent.Type.CONNECTION_SUSPENDED ){
                    System.out.println("连接失效");
                }else if(event.getType() == PathChildrenCacheEvent.Type.CONNECTION_RECONNECTED ){
                    System.out.println("重新连接");
                }else if(event.getType() == PathChildrenCacheEvent.Type.CONNECTION_LOST ){
                    System.out.println("连接失效后稍等一会儿执行");
                }
            }
        });
        System.in.read(); // 使线程阻塞
    }
}
