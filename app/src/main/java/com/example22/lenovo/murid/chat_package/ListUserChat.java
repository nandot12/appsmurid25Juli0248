package com.example22.lenovo.murid.chat_package;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example22.lenovo.murid.ApiService;
import com.example22.lenovo.murid.R;
import com.example22.lenovo.murid.notif.Client;
import com.example22.lenovo.murid.notif.Data;
import com.example22.lenovo.murid.notif.MyResponse;
import com.example22.lenovo.murid.notif.Sender;
import com.example22.lenovo.murid.notif.Token;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;

public class ListUserChat extends Fragment {


    ListView usersList;
    TextView noUsersText;
    ArrayList<String> al = new ArrayList<>();
    int totalUsers = 0;
    ProgressDialog pd;
    Unbinder unbinder;

    public ListUserChat() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_users, container, false);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        usersList = (ListView)view.findViewById(R.id.usersList);
        noUsersText = (TextView)view.findViewById(R.id.noUsersText);

        pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading...");
        pd.show();

        String url = "https://guruku2-572b2.firebaseio.com/admin.json";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                doOnSuccess(s);
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(getActivity());
        rQueue.add(request);


        usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserDetails.chatWith = al.get(position);
                // Toast.makeText(getContext(), al.get(position), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getContext(),DetailChat.class);
                intent.putExtra("username",al.get(position));
                startActivity(intent);

                //   startActivity(new Intent(getActivity(), DetailChat.class));
                //                \startActivity(new Intent(ListChat.this, Chat.class));
            }
        });




    }

    private void showNotif(final String username, final String pengirim, final String messageText) {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance() ;
        DatabaseReference databaseReference  = firebaseDatabase.getReference("Token_User");

        //  databaseReference.orderByChild("")

        databaseReference.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                Token token = dataSnapshot.getValue(Token.class);
                Data data = new Data(pengirim,username,R.mipmap.ic_launcher,pengirim +":"+messageText,"new Message","");
                Sender sender = new Sender(token.getToken(),data);

                ApiService apiService = Client.getClient("http://fcm.googleapis.com/").create(ApiService.class);

                apiService.sendNotif(sender).enqueue(new Callback<MyResponse>() {
                    @Override
                    public void onResponse(Call<MyResponse> call, retrofit2.Response<MyResponse> response) {
                        Log.d("send notif",response.body().toString());

                        //sign up baru ya pak di device
                        //suara bapak ilang

                        if(response.isSuccessful()){

                            int success = response.body().success;

                            if(success == 200){



                                Toast.makeText(getContext(), "succes", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<MyResponse> call, Throwable t) {

                        Log.d("eror notif",t.getLocalizedMessage());

                    }
                });



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }


    public void doOnSuccess(String s){
        try {
            JSONObject obj = new JSONObject(s);

            Iterator i = obj.keys();
            String key = "";

            while(i.hasNext()){
                key = i.next().toString();

                if(!key.equals(UserDetails.username)) {
                    al.add(key);
                }

                totalUsers++;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(totalUsers <1){
            noUsersText.setVisibility(View.VISIBLE);
            usersList.setVisibility(View.GONE);
        }
        else{
            noUsersText.setVisibility(View.GONE);
            usersList.setVisibility(View.VISIBLE);
            usersList.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, al));
        }

        pd.dismiss();
    }
}