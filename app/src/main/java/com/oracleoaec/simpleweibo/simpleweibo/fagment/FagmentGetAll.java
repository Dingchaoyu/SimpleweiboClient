package com.oracleoaec.simpleweibo.simpleweibo.fagment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.oracleoaec.simpleweibo.simpleweibo.Adapter.WeiBoAdapter;
import com.oracleoaec.simpleweibo.simpleweibo.BlogMoreActivity;
import com.oracleoaec.simpleweibo.simpleweibo.R;
import com.oracleoaec.simpleweibo.simpleweibo.Utils.HomePartent;
import com.oracleoaec.simpleweibo.simpleweibo.Utils.ReFlushListView;
import com.oracleoaec.simpleweibo.simpleweibo.enity.Blog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ycy on 16-4-12.
 */
public class FagmentGetAll extends Fragment implements AdapterView.OnItemClickListener
{
    protected Context context;
    protected ReFlushListView listView;
    protected RequestQueue requestQueue;
    protected List<Blog> bloglist;
    private WeiBoAdapter<Blog> adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getActivity();
        bloglist = new ArrayList<Blog>();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fagment_getall,container,false);
        listView = (ReFlushListView) view.findViewById(R.id.fagment_getall_listview);
        initRequest();
        listView.setOnItemClickListener(this);
        listView.setonRefreshListener(new ReFlushListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        bloglist.clear();
                        initRequest();
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        adapter.notifyDataSetChanged();
                        listView.onRefreshComplete();
                    }
                }.execute(null, null, null);
            }
        });
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        Blog blog = (Blog) adapter.getItem(i-1);
        int blog_id = blog.getBlog_id();
        Intent intent = new Intent(getActivity(), BlogMoreActivity.class);
        intent.putExtra("blog_id", blog_id);
        startActivity(intent);
    }

    private void initRequest()
    {
        requestQueue = Volley.newRequestQueue(context);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(HomePartent.getUrl() + "users/getallblog", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                try {
                    JSONArray array = new JSONArray(jsonArray.toString());
                    for (int i = 0; i <array.length() ; i++)
                    {
                        JSONObject object = array.getJSONObject(i);
                        String username = object.getString("user_name");
                        String content = object.getString("content");
                        String issueTime = object.getString("issueTime");
                        String photo = object.getString("user_photo");
                        int user_id = object.getInt("user_id");
                        int blog_id = object.getInt("id");
                        Blog blog = new Blog(issueTime,username,content,user_id);
                        blog.setUserphoto(photo);
                        blog.setBlog_id(blog_id);
                        Log.d("blog",blog.toString());
                        bloglist.add(blog);
                    }
                    Collections.reverse(bloglist);
                    adapter = new WeiBoAdapter(context,bloglist);
                    listView.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.i("TAG",volleyError.toString());
            }
        });
        requestQueue.add(jsonArrayRequest);
    }
}
