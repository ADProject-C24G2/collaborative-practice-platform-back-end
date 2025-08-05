package team8.ad.project.context;

public class BaseContext {

    public static ThreadLocal<Integer> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(int id) {
        threadLocal.set(id);
    }

    // public static int getCurrentId() {
    //     return threadLocal.get();
    // }

    public static int getCurrentId() {
        Integer id = threadLocal.get();
        return (id != null) ? id : 0; // 默认返回 0
    }

    public static void removeCurrentId() {
        threadLocal.remove();
    }

}
