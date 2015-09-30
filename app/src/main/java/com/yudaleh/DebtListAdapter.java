package com.yudaleh;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.fortysevendeg.swipelistview.SwipeListView;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.hb.views.PinnedSectionListView.PinnedSectionListAdapter;
import com.parse.FindCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
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
    private List<Debt> mDebts;
    private List<String> mDataHeaders;
    private HashMap<String, List<Debt>> mDataChildren;

    private static final int[] COLORS = new int[] {
            R.color.green_light, R.color.orange_light,
            R.color.blue_light, R.color.red_light };
    private DebtSwipeListAdapter mSwipeAdapter;

    @Override
    public int getGroupCount() {
        return this.mDataHeaders.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
//        return this.mDataChildren.get(this.mDataHeaders.get(groupPosition)).size();// FIXME: 30/09/2015
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.mDataHeaders.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.mDataChildren.get(this.mDataHeaders.get(groupPosition)).get(childPosition);
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
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup parent) {
        if(!isLastChild){
            return null;
//            debtTitle.setVisibility(View.GONE);
//            view.setVisibility(View.GONE);
        }

        ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.list_child, parent, false);
            holder = new ViewHolder();
            holder.childList = (SwipeListView) view.findViewById(R.id.child_list);
            view.setTag(holder);
            holder.childList.setAdapter(mSwipeAdapter);
        } else {
            holder = (ViewHolder) view.getTag();
        }

//        List<Debt> children = mDataChildren.get(mDataHeaders.get(groupPosition));
//        ArrayList<View> views = new ArrayList<>();
//        for (Debt child : children) {
//            View childView;
//            LayoutInflater infalInflater = (LayoutInflater) mContext
//                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            childView = infalInflater.inflate(R.layout.list_child, null);
//            TextView title = (TextView) childView.findViewById(R.id.debt_title);
//            title.setText(child.getTitle());
//            views.add(childView);
//        }

//        childList.addChildrenForAccessibility(views);

//        final Debt debt = (Debt) getChild(groupPosition, childPosition);
//
//
//
//        TextView debtTitle = holder.debtTitle;
//
//        debtTitle.setText(debt.getTitle());
//        if (debt.isDraft()) {
//            debtTitle.setTypeface(null, Typeface.ITALIC);
//            debtTitle.setTextColor(Color.RED);// TODO: 02/09/2015 GRAY
//
//        } else {
//            debtTitle.setTypeface(null, Typeface.NORMAL);
//            if (debt.getStatus() == Debt.STATUS_CREATED) {
//                debtTitle.setTextColor(Color.BLACK);
//            } else if (debt.getStatus() == Debt.STATUS_PENDING) {
//                debtTitle.setTextColor(Color.GREEN);
//            } else if (debt.getStatus() == Debt.STATUS_CONFIRMED) {
//                debtTitle.setTextColor(Color.BLUE);
//            } else if (debt.getStatus() == Debt.STATUS_RETURNED) {
//                debtTitle.setTextColor(Color.MAGENTA);
//            } else {
//                debtTitle.setTextColor(Color.YELLOW);
//            }
//
//        }

//        else{
//            debtTitle.setVisibility(View.VISIBLE);
//            view.setVisibility(View.VISIBLE);
//        }
        return view;
    }

    /*@Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup parent) {
        if(!isLastChild){
            return null;
        }
        else{
            return LayoutInflater.from(mContext).inflate(R.layout.list_child, parent, false);
        }
        final Debt debt = (Debt) getChild(groupPosition, childPosition);
        ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.list_child, parent, false);
//            view = LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);
            holder = new ViewHolder();
            holder.debtImage = (ImageView) view.findViewById(R.id.example_row_iv_image);
            holder.debtTitle = (TextView) view
                    .findViewById(R.id.debt_title);
            holder.debtDescription = (TextView) view.findViewById(R.id.example_row_tv_description);
            holder.action1 = (Button) view.findViewById(R.id.action_edit);
            holder.action2 = (Button) view.findViewById(R.id.action_message);
            holder.action3 = (Button) view.findViewById(R.id.action_call);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        TextView debtTitle = holder.debtTitle;
        TextView debtDescription = holder.debtDescription;
        ImageView debtImage = holder.debtImage;

        if (debt.getCurrencyPos() != Debt.NON_MONEY_DEBT_CURRENCY) {
            debtImage.setImageResource(R.drawable.dollar);
        } else {
            debtImage.setImageResource(R.drawable.box_closed_icon);// TODO: 25/09/2015 image / location
        }

        // Action 1:
        holder.action1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditView(debt);
            }
        });

        // Action 2:
        if (debt.getPhone() != null) {
            holder.action2.setText(R.string.action2_text_with_phone);
            holder.action2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showActionsDialog(debt);
                }
            });
        } else {
            holder.action2.setText(R.string.action2_text_no_phone);
            holder.action2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: 9/26/2015
                }
            });
        }
//        holder.action3.setText(mContext.getString(R.string.action3_text, debt.getDueDate()));
        holder.action3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SlideDateTimeListener listener = new SlideDateTimeListener() {

                    @SuppressWarnings("deprecation")
                    @Override
                    public void onDateTimeSet(Date date) {
                        date.setSeconds(0);
                        debt.setDueDate(date);
                    }

                    @Override
                    public void onDateTimeCancel() {

                    }
                };
                Date initDate;
                Date currDate = debt.getDueDate();
                if (currDate != null) {
                    initDate = currDate;
                } else {
                    initDate = new Date();
                }
                new SlideDateTimePicker.Builder(((AppCompatActivity) mContext).getSupportFragmentManager())
                        .setListener(listener)
                        .setInitialDate(initDate)
                        .setIndicatorColor(Color.RED)
                        .build()
                        .show();
            }
        });

        // TODO: 05/09/2015 remove info
*//*
        ParseUser author = debt.getAuthor();
        if(author!=null) {
            String token = author.getSessionToken();
            boolean isAuth = author.isAuthenticated();
            boolean isDataAvai = author.isDataAvailable();
            boolean isNew = author.isNew();
            boolean isDirty = author.isDirty();
            boolean isLinked = ParseAnonymousUtils.isLinked(author);
        }
//            String info = "\nauthor: "+author.getUsername()+"\nisAuth: "+isAuth+"\nisDataAvai: "+isDataAvai+"\nisNew: "+isNew+"\nisDirty: "+isDirty+"\ntoken: "+token+"\nisLinked: "+isLinked;
*//*

//String extra = "\n"+debt.getUuidString()+"<-"+debt.getOtherUuid(); // REMOVE: 24/09/2015
        debtTitle.setText(debt.getTitle());
        if (debt.isDraft()) {
            debtTitle.setTypeface(null, Typeface.ITALIC);
            debtTitle.setTextColor(Color.RED);// TODO: 02/09/2015 GRAY

        } else {
            debtTitle.setTypeface(null, Typeface.NORMAL);
            if (debt.getStatus() == Debt.STATUS_CREATED) {
                debtTitle.setTextColor(Color.BLACK);
            } else if (debt.getStatus() == Debt.STATUS_PENDING) {
                debtTitle.setTextColor(Color.GREEN);
            } else if (debt.getStatus() == Debt.STATUS_CONFIRMED) {
                debtTitle.setTextColor(Color.BLUE);
            } else if (debt.getStatus() == Debt.STATUS_RETURNED) {
                debtTitle.setTextColor(Color.MAGENTA);
            } else {
                debtTitle.setTextColor(Color.YELLOW);
            }

        }

        debtDescription.setText(debt.getOwner());
        return view;
    }*/

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
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
//        TextView debtTitle;
//        ImageView debtImage;
//        TextView debtDescription;
//        Button action1;
//        Button action2;
//        Button action3;

        SwipeListView childList;

    }

    DebtListAdapter(Context context, QueryFactory<Debt> queryFactory) {
        super(context, queryFactory);
        mSwipeAdapter= new DebtSwipeListAdapter(context,queryFactory);
        mContext = context;
        // Init data to prevent <code>NullPointerException</code>
        mDebts = new ArrayList<>();
        mDataHeaders = new ArrayList<>();
        mDataChildren = new HashMap<>();
        addOnQueryLoadListener(new OnQueryLoadListener<Debt>() {
            @Override
            public void onLoading() {

            }

            @Override
            public void onLoaded(List<Debt> debts, Exception e) {
                if (e == null) {
                    mDebts = debts;
                    mDataChildren = new HashMap<>();
                    for (Debt debt : debts) {
                        String phone = debt.getPhone();
                        if(!mDataChildren.containsKey(phone)){
                            List<Debt> debtItems= new ArrayList<>();
                            debtItems.add(debt);
                            mDataChildren.put(phone,debtItems);
//                            ArrayList<View> debtViews= new ArrayList<>();// REMOVE: 30/09/2015
//                            View debtView =
//                            debtViews.add(debt);
//                            mDataChildrenViews.put(phone,debtViews);
                        }
                        else {
                            mDataChildren.get(phone).add(debt);
                        }
                    }
                    mDataHeaders = new ArrayList<>(mDataChildren.keySet());// TODO: 9/29/2015 sort
                }
            }
        });
    }

    /*@Override
    public View getItemView(final Debt debt, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);
            holder = new ViewHolder();
            holder.debtImage = (ImageView) view.findViewById(R.id.example_row_iv_image);
            holder.debtTitle = (TextView) view
                    .findViewById(R.id.debt_title);
            holder.debtDescription = (TextView) view.findViewById(R.id.example_row_tv_description);
            holder.action1 = (Button) view.findViewById(R.id.action_edit);
            holder.action2 = (Button) view.findViewById(R.id.action_message);
            holder.action3 = (Button) view.findViewById(R.id.action_call);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        TextView debtTitle = holder.debtTitle;
        TextView debtDescription = holder.debtDescription;
        ImageView debtImage = holder.debtImage;

        if (debt.getCurrencyPos() != Debt.NON_MONEY_DEBT_CURRENCY) {
            debtImage.setImageResource(R.drawable.dollar);
        } else {
            debtImage.setImageResource(R.drawable.box_closed_icon);// TODO: 25/09/2015 image / location
        }

        // Action 1:
        holder.action1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditView(debt);
            }
        });

        // Action 2:
        if (debt.getPhone() != null) {
            holder.action2.setText(R.string.action2_text_with_phone);
            holder.action2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showActionsDialog(debt);
                }
            });
        } else {
            holder.action2.setText(R.string.action2_text_no_phone);
            holder.action2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: 9/26/2015
                }
            });
        }
//        holder.action3.setText(mContext.getString(R.string.action3_text, debt.getDueDate()));
        holder.action3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SlideDateTimeListener listener = new SlideDateTimeListener() {

                    @SuppressWarnings("deprecation")
                    @Override
                    public void onDateTimeSet(Date date) {
                        date.setSeconds(0);
                        debt.setDueDate(date);
                    }

                    @Override
                    public void onDateTimeCancel() {

                    }
                };
                Date initDate;
                Date currDate = debt.getDueDate();
                if (currDate != null) {
                    initDate = currDate;
                } else {
                    initDate = new Date();
                }
                new SlideDateTimePicker.Builder(((AppCompatActivity) mContext).getSupportFragmentManager())
                        .setListener(listener)
                        .setInitialDate(initDate)
                        .setIndicatorColor(Color.RED)
                        .build()
                        .show();
            }
        });

        // TODO: 05/09/2015 remove info
*//*
        ParseUser author = debt.getAuthor();
        if(author!=null) {
            String token = author.getSessionToken();
            boolean isAuth = author.isAuthenticated();
            boolean isDataAvai = author.isDataAvailable();
            boolean isNew = author.isNew();
            boolean isDirty = author.isDirty();
            boolean isLinked = ParseAnonymousUtils.isLinked(author);
        }
//            String info = "\nauthor: "+author.getUsername()+"\nisAuth: "+isAuth+"\nisDataAvai: "+isDataAvai+"\nisNew: "+isNew+"\nisDirty: "+isDirty+"\ntoken: "+token+"\nisLinked: "+isLinked;
*//*

//String extra = "\n"+debt.getUuidString()+"<-"+debt.getOtherUuid(); // REMOVE: 24/09/2015
        debtTitle.setText(debt.getTitle());
        if (debt.isDraft()) {
            debtTitle.setTypeface(null, Typeface.ITALIC);
            debtTitle.setTextColor(Color.RED);// TODO: 02/09/2015 GRAY

        } else {
            debtTitle.setTypeface(null, Typeface.NORMAL);
            if (debt.getStatus() == Debt.STATUS_CREATED) {
                debtTitle.setTextColor(Color.BLACK);
            } else if (debt.getStatus() == Debt.STATUS_PENDING) {
                debtTitle.setTextColor(Color.GREEN);
            } else if (debt.getStatus() == Debt.STATUS_CONFIRMED) {
                debtTitle.setTextColor(Color.BLUE);
            } else if (debt.getStatus() == Debt.STATUS_RETURNED) {
                debtTitle.setTextColor(Color.MAGENTA);
            } else {
                debtTitle.setTextColor(Color.YELLOW);
            }

        }

        debtDescription.setText(debt.getOwner());
        return view;
    }*/


    /**
     * Show a confirmation push notification dialog, with an option to call the owner.
     */
    private void showActionsDialog(final Debt debt) {
        int array;
        if (!ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {
            array = R.array.contact_actions_array_logged_in;
        } else {
            array = R.array.contact_actions_array_logged_out;
        }
        (new AlertDialog.Builder(mContext))
                .setTitle(R.string.contact_actions_dialog_title_action)
                .setItems(array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        switch (whichButton) {
                            case ACTION_CHAT:
                                openConversationByPhone(debt);
                                break;
                            case ACTION_CALL:
                                Intent dial = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + debt.getPhone()));
                                mContext.startActivity(dial);
                                break;
                            case ACTION_SMS:
                                Intent sms = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", debt.getPhone(), null));
                                mContext.startActivity(sms);
                                break;

                        }
                    }
                })
                .show();
    }

//    @Override public int getViewTypeCount() {
//        return 2;
//    }
//
//    @Override public int getItemViewType(int position) {
//        return getItem(position).getStatus();// TODO: 29/09/2015
//    }
//
//    @Override
//    public boolean isItemViewTypePinned(int viewType) {
//        return false;
//    }

    public void openConversationByPhone(Debt debt) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("phone", debt.getAuthorPhone());
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> user, com.parse.ParseException e) {
                if (e == null) {
                    Intent intent = new Intent(mContext, MessagingActivity.class);
                    intent.putExtra("RECIPIENT_ID", user.get(0).getObjectId());
                    mContext.startActivity(intent);
                } else {
                    Toast.makeText(mContext,
                            "Error finding that user",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Helper methods: -----------------------------------------------------------------------------
    private void openEditView(Debt debt) {
        Intent i = new Intent(mContext, EditDebtActivity.class);
        i.putExtra(Debt.KEY_UUID, debt.getUuidString());
        i.putExtra(Debt.KEY_TAB_TAG, debt.getTabTag());
        mContext.startActivity(i);
    }

    static class Item {

        public static final int ITEM = 0;
        public static final int SECTION = 1;

        public final int type;
        public final String text;

        public int sectionPosition;
        public int listPosition;

        public Item(int type, String text) {
            this.type = type;
            this.text = text;
        }

        @Override public String toString() {
            return text;
        }

    }

}
