package com.yudaleh;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.parse.ParseQueryAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;


/**
 * Adapter between the list and the user's debts.
 */
class DebtListAdapter extends ParseQueryAdapter<Debt> implements /*PinnedSectionListAdapter,*/ ExpandableListAdapter {

    static final int ACTION_CALL = 0;
    static final int ACTION_SMS = 1;
    static final int ACTION_CHAT = 2;

    private final Context mContext;
    private final ArrayList<Integer> mColors;
    private HashMap<String, List<ArrayAdapter<Debt>>> mChildrenAdapters;
    private List<Contact> mDataHeaders;
    private List<Debt> mSelectedData;
    private HashMap<String, Integer> mOwnerNamesCount;
    private HashMap<String, Integer> mOwnerNamesCountNoPhone;
    private HashMap<String, List<Debt>> mDataChildren;

    // TODO: 29/09/2015 pinned heads
//    @Override public int getViewTypeCount() {
//        return 2;
//    }
//
//    @Override public int getItemViewType(int position) {
//        return getItem(position).getStatus();
//    }
//
//    @Override
//    public boolean isItemViewTypePinned(int viewType) {
//        return false;
//    }

    @Override
    public int getGroupCount() {
        return this.mDataHeaders.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.mDataChildren.get(this.mDataHeaders.get(groupPosition).getMapKey()).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.mDataHeaders.get(groupPosition);
    }

    private double countTotalMoney(String key) {
        List<Debt> debts = mDataChildren.get(key);
        if (debts == null || debts.size() == 0) {
            return 0;
        }
        double total = 0;
        for (Debt debt : debts) {
            total += debt.getMoneyAmount();
        }
        return total;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.mDataChildren.get(this.mDataHeaders.get(groupPosition).getMapKey()).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        Contact owner = (Contact) getGroup(groupPosition);
        String phone = owner.getOwnerPhone();
        String name = owner.getOwnerName();

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        String headerTitle = name;
        if (phone != null && mOwnerNamesCount.get(name) > 1) {
            headerTitle += " (" + phone + ")";
        }
        // TODO: 05/10/2015 unique identifier for not merged non-money debts

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        if (owner.getTotalMoney() > 0) {
            convertView.setBackgroundColor(owner.getColor());
        } else {
            convertView.setBackgroundColor(Color.LTGRAY);
        }
        return convertView;
    }

    public int convertDpToPixel(float dp) {
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View view, ViewGroup parent) {
        // FIXME: 03/10/2015 called too many times
        ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.list_child, parent, false);
            holder = new ViewHolder();
            holder.childList = (SwipeListView) view.findViewById(R.id.child_list);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        final SwipeListView swipeListView = holder.childList;
//        swipeListView.color(mColors.get(0));
        swipeListView.setSwipeListViewListener(new BaseSwipeListViewListener() {

            @Override
            public void onChoiceChanged(int position, boolean selected) {
/*                Debt debt = (Debt) getChild(groupPosition, childPosition);
                if (selected) {
                    swipeListView.dispatchSetSelected(true);
                    swipeListView.setSelection(position);
                    swipeListView.setItemChecked(position, selected);
                    mSelectedData.add(debt);
                } else {
                    swipeListView.dispatchSetSelected(false);
                    mSelectedData.remove(debt);
                }*/// TODO: 07/10/2015 make sure hidden items stay selected
//                super.onChoiceChanged(position, selected);
            }

            @Override
            public void onOpened(int position, boolean toRight) {
            }

            @Override
            public void onClosed(int position, boolean fromRight) {
            }

            @Override
            public void onListChanged() {
            }

            @Override
            public void onMove(int position, float x) {
            }

            @Override
            public void onStartOpen(int position, int action, boolean right) {
                Log.d("swipe", String.format("onStartOpen %d - action %d", position, action));
            }

            @Override
            public void onStartClose(int position, boolean right) {
                Log.d("swipe", String.format("onStartClose %d", position));
            }

            @Override
            public void onClickFrontView(int position) {
                Log.d("swipe", String.format("onClickFrontView %d", position));
            }

            @Override
            public void onClickBackView(int position) {
                Log.d("swipe", String.format("onClickBackView %d", position));
            }

            @Override
            public void onDismiss(int[] reverseSortedPositions) {
                for (int position : reverseSortedPositions) {
//                    System.out.println("dismiss: " + groupPosition + ", " + position);// REMOVE: 07/10/2015
                }
//                adapter.notifyDataSetChanged();
            }

        });
        holder.childList.setAdapter(getAdapter(groupPosition, childPosition));

        return view;
    }

    private int getCountSelected() {
        return mSelectedData.size();
    }

    private void dismissSelected() {
        // TODO: 07/10/2015
    }


    private ArrayAdapter<Debt> getAdapter(int groupPosition, int childPosition) {
        return this.mChildrenAdapters.get(this.mDataHeaders.get(groupPosition).getMapKey()).get(childPosition);
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {

    }

    @Override
    public void onGroupCollapsed(int groupPosition) {

    }

    @Override
    public long getCombinedChildId(long groupId, long childId) {
        return 0;
    }

    @Override
    public long getCombinedGroupId(long groupId) {
        return 0;
    }

    private class ViewHolder {
        SwipeListView childList;
    }

    public void update() {
        loadObjects();
    }


    DebtListAdapter(Context context, QueryFactory<Debt> queryFactory) {
        super(context, queryFactory);// TODO: 10/5/2015 add order by _
        mContext = context;

        mColors = new ArrayList<>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            mColors.add(c);
        for (int c : ColorTemplate.JOYFUL_COLORS)
            mColors.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS)
            mColors.add(c);
        for (int c : ColorTemplate.LIBERTY_COLORS)
            mColors.add(c);
        for (int c : ColorTemplate.PASTEL_COLORS)
            mColors.add(c);
        mColors.add(ColorTemplate.getHoloBlue());

        // Init data to prevent NullPointerException
        mDataHeaders = new ArrayList<>();
        mSelectedData = new ArrayList<>();
        mDataChildren = new HashMap<>();
        mChildrenAdapters = new HashMap<>();
        mOwnerNamesCount = new HashMap<>();
        mOwnerNamesCountNoPhone = new HashMap<>();

        addOnQueryLoadListener(new OnQueryLoadListener<Debt>() {
            @Override
            public void onLoading() {

            }

            @Override
            public void onLoaded(List<Debt> debts, Exception e) {
                if (e == null) {
                    extractData(debts);
                    notifyDataSetChanged(); // FIXME: 03/10/2015 index out of bounds when removed
                }
            }
        });
    }

    private void extractData(List<Debt> debts) {
        mDataChildren = new HashMap<>();
        mChildrenAdapters = new HashMap<>();
        mOwnerNamesCount = new HashMap<>();
        mOwnerNamesCountNoPhone = new HashMap<>();
        mDataHeaders = new ArrayList<>();
        mSelectedData = new ArrayList<>();

        for (Debt debt : debts) {
            String phone = debt.getOwnerPhone();
            String name = debt.getOwnerName();
            Contact contact = new Contact(phone, name);
            String key = contact.getMapKey();

            // TODO: 03/10/2015 update existing adapters
            ArrayList<Debt> singleItemList = new ArrayList<>();
            singleItemList.add(debt);
            ArrayAdapter<Debt> swipeAdapter = new DebtSwipeListAdapter(mContext, R.layout.list_item, singleItemList);
            if (!mDataChildren.containsKey(key)) {
                List<Debt> debtItems = new ArrayList<>();
                debtItems.add(debt);
                mDataChildren.put(key, debtItems);

                List<ArrayAdapter<Debt>> debtAdapters = new ArrayList<>();
                debtAdapters.add(swipeAdapter);
                mChildrenAdapters.put(key, debtAdapters);

                mDataHeaders.add(contact);
            } else {
                mDataChildren.get(key).add(debt);
                mChildrenAdapters.get(key).add(swipeAdapter);
            }

            for (Contact c : mDataHeaders) {
                c.setTotalMoney(countTotalMoney(c.getMapKey()));
            }
            if (phone != null) {
                if (!mOwnerNamesCount.containsKey(name)) {
                    mOwnerNamesCount.put(name, 1);
                } else {
                    mOwnerNamesCount.put(name, mOwnerNamesCount.get(name) + 1);
                }
            } else {
                if (!mOwnerNamesCountNoPhone.containsKey(name)) {
                    mOwnerNamesCountNoPhone.put(name, 1);
                } else {
                    // TODO: 05/10/2015 dialog
                    mOwnerNamesCountNoPhone.put(name, mOwnerNamesCountNoPhone.get(name) + 1);
                }
            }
        }
        Collections.sort(mDataHeaders, new Comparator<Contact>() {
            @Override
            public int compare(Contact lhs, Contact rhs) {
                if (lhs.equals(rhs)) {
                    return 0;
                }
                if (lhs.getTotalMoney() > rhs.getTotalMoney()) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
        int i = 0;
        int numColors = mColors.size();
        for (Contact contact : mDataHeaders) {
            if (contact.getTotalMoney() > 0) {
                contact.setColor(mColors.get(i % numColors));
                i++;
            }
        }
    }


    // Helper methods: -----------------------------------------------------------------------------
    private void openEditView(Debt debt) {// TODO: 04/10/2015 onclick
        Intent i = new Intent(mContext, EditDebtActivity.class);
        i.putExtra(Debt.KEY_UUID, debt.getUuidString());
        i.putExtra(Debt.KEY_TAB_TAG, debt.getTabTag());
        mContext.startActivity(i);
    }
}
