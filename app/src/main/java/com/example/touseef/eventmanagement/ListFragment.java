package com.example.touseef.eventmanagement;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ListFragment extends android.support.v4.app.Fragment {
    public ListFragment() {
        // Required empty public constructor
    }
    View v;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ChildEventListener childEventListener;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         v= inflater.inflate(R.layout.activity_list_fragment, container, false);
         return v;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        final Context context = getContext();
        FloatingActionButton fab = (FloatingActionButton)getActivity().findViewById(R.id.fab);
        Button b = (Button)getActivity().findViewById(R.id.send);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("list");
        final ListView listView = getActivity().findViewById(R.id.list_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getContext(),MainActivity.class);
                startActivity(i);
            }
        });
        List<ListEvent> list = new ArrayList<>();
        final ListEventAdapter listEventAdapter = new ListEventAdapter(getContext(),R.layout.list_item,list);
        listView.setAdapter(listEventAdapter);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View customView = layoutInflater.inflate(R.layout.popuplayout,null);
                final PopupWindow mPopupWindow =new PopupWindow(
                        customView,
                        680,400
                );
                mPopupWindow.setFocusable(true);
                if(Build.VERSION.SDK_INT>=21){
                    mPopupWindow.setElevation(20);
                    mPopupWindow.setOutsideTouchable(true);
                    applyDim((ViewGroup)getView(), 0.5f);
                }
                mPopupWindow.update();
                Button closeButton = (Button) customView.findViewById(R.id.button);
                final EditText editText = (EditText)customView.findViewById(R.id.editText1);
                final EditText editText1 = (EditText)customView.findViewById(R.id.editText2);
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPopupWindow.dismiss();
                        clearDim((ViewGroup)getView());
                    }
                });
                mPopupWindow.showAtLocation(customView, Gravity.CENTER,0,0);
                final Button ok = (Button)customView.findViewById(R.id.button2);
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.toString().trim().length() > 0) {
                            ok.setEnabled(true);
                        } else {
                            ok.setEnabled(false);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});

                Date c = Calendar.getInstance().getTime();
                System.out.println("Current time => " + c);
                int hr = c.getHours();
                int min = c.getMinutes();
                SimpleDateFormat df = new SimpleDateFormat("dd-MMM");
                String formattedDate = df.format(c);
                final String datetime = formattedDate + " " +Integer.toString(hr) + ":" + Integer.toString(min);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String s1 = editText.getText().toString();
                        String s2 = editText1.getText().toString();
                        Toast.makeText(getContext(),"Task added",Toast.LENGTH_SHORT).show();
                        ListEvent listEvent = new ListEvent(s1,s2,"false",datetime);
                        editText.setText("");
                        editText1.setText("");
                        databaseReference.push().setValue(listEvent);
                        mPopupWindow.dismiss();
                        clearDim((ViewGroup)getView());

                    }
                });
            }
        });
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                TextView tv = (TextView)getActivity().findViewById(R.id.text1);
                tv.setVisibility(View.GONE);
                ListEvent listEvent = dataSnapshot.getValue(ListEvent.class);
                listEventAdapter.add(listEvent);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReference.addChildEventListener(childEventListener);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListAdapter adapter  = listView.getAdapter();
                int itemscount       = adapter.getCount();
                int allitemsheight   = 0;
                List<Bitmap> bmps    = new ArrayList<Bitmap>();

                for (int i = 0; i < itemscount; i++) {

                    View childView      = adapter.getView(i, null, listView);
                    childView.measure(View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.EXACTLY),
                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

                    childView.layout(0, 0, childView.getMeasuredWidth(), childView.getMeasuredHeight());
                    childView.setDrawingCacheEnabled(true);
                    childView.buildDrawingCache();
                    bmps.add(childView.getDrawingCache());
                    allitemsheight+=childView.getMeasuredHeight();
                }

                Bitmap bigbitmap    = Bitmap.createBitmap(listView.getMeasuredWidth(), allitemsheight, Bitmap.Config.ARGB_8888);
                Canvas bigcanvas    = new Canvas(bigbitmap);

                Paint paint = new Paint();
                int iHeight = 0;

                for (int i = 0; i < bmps.size(); i++) {
                    Bitmap bmp = bmps.get(i);
                    bigcanvas.drawBitmap(bmp, 0, iHeight, paint);
                    iHeight+=bmp.getHeight();

                    bmp.recycle();
                    bmp=null;
                }
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bigbitmap.compress(Bitmap.CompressFormat.JPEG,100,bytes);
                String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), bigbitmap, "Title", null);
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.setType("image/*");
                sendIntent.putExtra(Intent.EXTRA_STREAM,Uri.parse(path));
                sendIntent.setPackage("com.whatsapp");
                startActivity(sendIntent);
            }
        });
    }
    public static void applyDim(@NonNull ViewGroup parent, float dimAmount){
        Drawable dim = new ColorDrawable(Color.BLACK);
        dim.setBounds(0, 0, parent.getWidth(), parent.getHeight());
        dim.setAlpha((int) (255 * dimAmount));

        ViewGroupOverlay overlay = parent.getOverlay();
        overlay.add(dim);
    }

    public static void clearDim(@NonNull ViewGroup parent) {
        ViewGroupOverlay overlay = parent.getOverlay();
        overlay.clear();
    }
}
