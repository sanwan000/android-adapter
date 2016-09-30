package cn.yhq.adapter.expand;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import java.util.List;

import cn.yhq.adapter.core.IItemViewKeyPolicy;
import cn.yhq.adapter.core.IItemViewSelector;
import cn.yhq.adapter.core.ItemViewFactory;
import cn.yhq.adapter.core.ViewHolder;

public final class GroupItemViewProviderFactory<T>
    extends ItemViewFactory<BaseExpandableListAdapter, GroupItemViewProvider1<T>> {
  private IGroupItemViewProviderKeyPolicy<T> mGroupItemViewProviderKeyPolicy;

  public GroupItemViewProviderFactory(Context context, BaseExpandableListAdapter adapter) {
    super(context, adapter);
  }

  public void setGroupItemViewProviderKeyPolicy(
      IGroupItemViewProviderKeyPolicy<T> groupItemViewProviderKeyPolicy) {
    this.mGroupItemViewProviderKeyPolicy = groupItemViewProviderKeyPolicy;
  }

  private GroupItemViewProvider1<T> obtainItemViewProvider(final int position, final T entity) {
    return this.obtainItemView(new IItemViewKeyPolicy() {
      @Override
      public int getItemViewKey() {
        if (mGroupItemViewProviderKeyPolicy != null) {
          return mGroupItemViewProviderKeyPolicy.getItemViewTypeKey(position, entity);
        }
        return -1;
      }
    }, new IItemViewSelector<GroupItemViewProvider1<T>>() {
      @Override
      public boolean isForItemView(GroupItemViewProvider1<T> itemView) {
        if (itemView instanceof IGroupItemViewProviderSelector) {
          return ((IGroupItemViewProviderSelector) itemView).isForProvider(position, entity);
        }
        return false;
      }
    });
  }

  public int getItemViewType(int position, T entity) {
    return obtainItemViewProvider(position, entity).getType();
  }

  public List<GroupItemViewProvider1<T>> getAllItemViewProvider() {
    return this.getAllItemView();
  }

  public int getItemViewTypeCount() {
    return this.getItemViewSize();
  }

  public View setupView(int position, T entity, View convertView, ViewGroup parent,
      boolean isExpanded) {
    try {
      // 获取该item类型的视图提供器
      IGroupItemViewProvider<T> itemViewProvider = this.obtainItemViewProvider(position, entity);
      // 获取视图id
      int layoutId = itemViewProvider.getItemViewLayoutId();
      // 获取viewholder
      ViewHolder viewHolder = ViewHolder.get(mContext, convertView, parent, layoutId, position);
      // 组装视图
      itemViewProvider.setupView(viewHolder, position, entity, isExpanded);
      return viewHolder.getConvertView();
    } catch (Exception | Error e) {
      e.printStackTrace();
      return new View(mContext);
    }
  }

  public GroupItemViewProvider1<T> getItemViewProviderByKey(int key) {
    return this.getItemViewByKey(key);
  }
}
