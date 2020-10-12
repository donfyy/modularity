```java
// TODO  这就是要用 APT 动态生成的代码
public class ARouter$$Path$$personal implements ARouterPath {

    @Override
    public Map<String, RouterBean> getPathMap() {
        Map<String, RouterBean> pathMap = new HashMap<>();

        pathMap.put("/personal/Personal_MainActivity",
                RouterBean.create(RouterBean.TypeEnum.ACTIVITY,
                                  Order_MainActivity.class,
                           "/personal/Personal_MainActivity",
                          "personal"));

        pathMap.put("/personal/Order_Main2Activity",
                RouterBean.create(RouterBean.TypeEnum.ACTIVITY,
                        Personal_Main2Activity.class,
                        "/personal/Personal_Main2Activity",
                        "personal"));
        return pathMap;
    }
}
```

例如：Personal Group：

```java
// TODO  这就是要用 APT 动态生成的代码
public class ARouter$$Group$$personal implements ARouterGroup {

    @Override
    public Map<String, Class<? extends ARouterPath>> getGroupMap() {
        Map<String, Class<? extends ARouterPath>> groupMap = new HashMap<>();
        groupMap.put("personal", ARouter$$Path$$personal.class);
        return groupMap;
    }
}
```



