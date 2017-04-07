package com.mrs.mrs.multisupportrecyclerview.recyclerview.multisupport;

/**
 * Created by mrs on 2017/4/7.
 */

/**
 * 根据 item类型判断 给出 该item布局layout  id。
 * <p>
 * <p>
 * MultiTypeSupport SUPPORT = new MultiTypeSupport() {
 * @param <T>
 * @Override public int getTypeLayoutRes(JavaBean data, int pos) {
 * if(data.type==1){
 *      return R.layout.layout1;
 * }else if(data.type==2)
 *      return R.layout.layout2;
 * else
 *     return R.layout.layout3;
 * };
 */
public interface MultiTypeSupport<T> {

    int getTypeLayoutRes(T data, int pos);
}
