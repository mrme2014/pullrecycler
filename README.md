# MultiSupportRecyclerView
- 支持上拉加载
- 下拉刷新
- 空状态错误状转换
- 添加多个header,footer
- 优雅的实现多条目类型列表
- 配合[itemtouchhelper]:https://github.com/mrme2014/ItemTouchHelper
  来实现非侵入式侧滑菜单，长按拖拽
- IM消息列表软键盘弹起时消息的定位（看QQ软键盘弹起）
- 列表滚动动画
- 统一LinearLayoutManager,GridLayoutManager,StaggeredGridLayoutManager常用api
# 运行截图
gif录制效果比较渣,录出来竟然花屏了

![image](https://github.com/mrme2014/MultiSupportRecyclerView/raw/master/art/gif2.gif)

# 添加依赖
```java
dependencies{

       compile 'com.qiaomu.library:pullrecycler:1.0.5'
}

```
# 布局使用
```java
 <com.qiaomu.libmultirecyclerview.PullRecycler
        android:id="@+id/PullRecycler"
        android:layout_width="match_parent"
        android:layout_height="match_parent">

    </com.qiaomu.libmultirecyclerview.PullRecycler>
```
# 示例
```java
 public void loadFailed() {
        //刷新
        if (mCurPage == 1) {
            mDatas.clear();
            //空数据
            if (mDatas == null || mDatas.size() == 0) {
                mPullRecycler.showEmptyView();
                //设置空白页面
            }
        } else {
            //加载更多
            mPullRecycler.setEnableLoadMore(false);
        }
        mPullRecycler.setOnRefreshCompeleted();
        mPullRecycler.showLoadOverView();
    } 
```
# 更多使用详情请查看demo
