package net.wangtu.android.common.view.refresh;

/**
 * 刷新事件
 * @author zhangxz
 *
 */
public class DefaultRefreshViewEvent implements RefreshViewEvent{

	@Override
	public void startVerticalRefresh() {
		
	}

	@Override
	public void stopVerticalRefresh() {
		
	}

	@Override
	public boolean canForgiveVerticalRefresh() {
		return false;
	}

	@Override
	public boolean showVerticalRefresh() {
		return false;
	}

	@Override
	public void startVerticalLoad() {
		//LogUtil.debug("触发加载");
	}

	@Override
	public void stopVerticalLoad() {
	}

	@Override
	public boolean showVerticalLoad() {
		return false;
	}

	@Override
	public void startHorizonLoading(int direct) {
		
	}

	@Override
	public void stopHorizonLoading() {
		
	}

}
