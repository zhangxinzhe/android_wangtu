package net.wangtu.android.common.view.refresh;

/**
 * 刷新事件
 * @author zhangxz
 *
 */
public interface RefreshViewEvent {
	/*============================垂直下拉刷新事件=================================*/
	/**
	 * 开始下拉刷新
	 */
	public void startVerticalRefresh();
	
	/**
	 * 主动放弃下拉刷新
	 */
	public void stopVerticalRefresh();
	
	/**
	 * 是否允许放弃下拉刷新
	 * @return
	 */
	public boolean canForgiveVerticalRefresh();
	
	/**
	 * 是否显示下拉刷新
	 * @return
	 */
	public boolean showVerticalRefresh();
	
	/*============================垂直滚动加载事件=================================*/
	/**
	 * 开始滚动加载
	 */
	public void startVerticalLoad();
	
	/**
	 * 主动取消加载（暂时没用）
	 */
	public void stopVerticalLoad();
	
	/**
	 * 是否显示滚动加载
	 * @return
	 */
	public boolean showVerticalLoad();
	
	/*============================水平滑动事件=================================*/
	
	/**
	 * 开始水平滑动
	 * @param direct 方向:TO_LEFT,TO_RIGHT
	 */
	public void startHorizonLoading(int direct);//开始水平加载
	
	/**
	 * 结束水平滑动
	 */
	public void stopHorizonLoading();//结束水平加载
}
