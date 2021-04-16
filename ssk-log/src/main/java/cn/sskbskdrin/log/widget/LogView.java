package cn.sskbskdrin.log.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.ArcShape;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Created by ex-keayuan001 on 2019-08-14.
 *
 * @author ex-keayuan001
 */
class LogView extends FrameLayout {

    private static final String[] LEVEL = {"Verbose", "Debug", "Info", "Warn", "Error", "Assert"};

    private ListView listView;
    private FrameLayout contentView;
    private TextView floatView;

    private ViewFilter filter;
    private int level;
    private String content;
    private boolean isBottom;

    private Log[] mList;
    private BaseAdapter adapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return mList == null ? 0 : mList.length;
        }

        @Override
        public Log getItem(int position) {
            return mList == null ? null : mList[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new TextView(getContext());
                ((TextView) convertView).setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            }
            Log log = getItem(position);
            if (log != null) {
                TextView view = (TextView) convertView;
                view.setText(log.getContent());
                view.setTextColor(log.color());
            }
            return convertView;
        }
    };

    public LogView(Context context) {
        this(context, null);
    }

    public LogView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LogView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayoutParams(new LayoutParams(-1, -1));

        initView();
    }

    private void initView() {
        contentView = new FrameLayout(getContext());
        contentView.setBackgroundColor(Color.WHITE);
        contentView.setVisibility(GONE);
        initFilterView();

        listView = new ListView(getContext());
        listView.setDivider(new ColorDrawable(0x30000000));
        listView.setDividerHeight(dpToPx(1));
        listView.setSelector(new ColorDrawable(0));
        listView.setAdapter(adapter);
        LayoutParams lp = new LayoutParams(-1, -1);
        lp.topMargin = dpToPx(40);
        contentView.addView(listView, lp);

        addView(contentView, new LayoutParams(-1, getResources().getDisplayMetrics().heightPixels * 2 / 3));
        initFloatView();
    }

    private void initFilterView() {
        LinearLayout filterView = new LinearLayout(getContext());
        filterView.setOrientation(LinearLayout.HORIZONTAL);
        filterView.setBackgroundColor(Color.GRAY & 0x80ffffff);

        ImageView image = new ImageView(getContext());
        image.setScaleType(ImageView.ScaleType.CENTER);
        image.setPadding(dpToPx(4), 0, dpToPx(4), 0);
        image.setImageResource(android.R.drawable.ic_menu_delete);
        image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                filter(true);
            }
        });

        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(dpToPx(36), -1);
        filterView.addView(image, llp);

        image = new ImageView(getContext());
        image.setScaleType(ImageView.ScaleType.CENTER);
        image.setImageResource(android.R.drawable.arrow_down_float);
        image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isBottom = !isBottom;
                v.setBackgroundColor(isBottom ? Color.GRAY : Color.TRANSPARENT);
                updateList(mList);
            }
        });
        image.performClick();
        filterView.addView(image, new LinearLayout.LayoutParams(dpToPx(36), -1));

        Spinner spinner = new Spinner(getContext());
        spinner.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, LEVEL));
        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                level = position;
                filter(false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        filterView.addView(spinner, dpToPx(120), dpToPx(40));

        EditText editText = new EditText(getContext());
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                content = s.toString();
                filter(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        llp = new LinearLayout.LayoutParams(0, -2, 1);
        llp.gravity = Gravity.RIGHT;
        filterView.addView(editText, llp);

        contentView.addView(filterView, new LayoutParams(-1, dpToPx(40)));
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initFloatView() {
        floatView = new TextView(getContext());
        floatView.setText("log");
        floatView.setGravity(Gravity.CENTER);
        floatView.setTextColor(Color.WHITE);
        floatView.setTextSize(18);
        Drawable drawable = new ShapeDrawable(new ArcShape(0, 360));
        drawable.setColorFilter(new PorterDuffColorFilter(0xa000b5ff, PorterDuff.Mode.SRC_IN));
        floatView.setBackgroundDrawable(drawable);
        floatView.setOnTouchListener(new OnTouchListener() {
            PointF last = new PointF();
            PointF move = new PointF();
            PointF parentSize = null;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (parentSize == null) {
                            parentSize = new PointF(((View) v.getParent()).getWidth(),
                                ((View) v.getParent()).getHeight());
                        }
                        last = new PointF(event.getX(), event.getY());
                        move.set(0, 0);
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        move.x += event.getX() - last.x;
                        move.y += event.getY() - last.y;
                        float newX = v.getX() + event.getX() - last.x;
                        float newY = v.getY() + event.getY() - last.y;

                        if (newX > 0 && newX < parentSize.x - v.getWidth()) {
                            v.setX(newX);
                        }
                        if (newY > 0 && newY < parentSize.y - v.getHeight()) {
                            v.setY(newY);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (v.getX() < parentSize.x / 2) {
                            v.setX(0);
                        } else {
                            v.setX(parentSize.x - v.getWidth());
                        }
                        if (Math.abs(move.x) < 5 || Math.abs(move.y) < 5) {
                            int show = contentView.getVisibility();
                            contentView.setVisibility(show == View.VISIBLE ? View.GONE : View.VISIBLE);
                        }
                    default:
                }
                return true;
            }
        });


        LayoutParams lp = new LayoutParams(dpToPx(48), dpToPx(48));
        lp.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
        addView(floatView, lp);
    }

    private int dpToPx(int dp) {
        return (int) (getResources().getDisplayMetrics().density * dp + 0.5f);
    }

    void setFilter(ViewFilter filter) {
        this.filter = filter;
    }

    private void filter(boolean clear) {
        if (filter != null) {
            filter.onFilter(clear, level, content);
        }
    }

    public void updateList(Log[] data) {
        if (data == null) {
            data = new Log[0];
        }
        mList = data;
        adapter.notifyDataSetChanged();
        if (isBottom && mList.length > 0) {
            listView.smoothScrollToPosition(mList.length - 1);
        }
    }
}
