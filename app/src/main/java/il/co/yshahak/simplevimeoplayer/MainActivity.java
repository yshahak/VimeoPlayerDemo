package il.co.yshahak.simplevimeoplayer;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.vimeo.networking.Configuration;
import com.vimeo.networking.VimeoClient;
import com.vimeo.networking.callbacks.AuthCallback;
import com.vimeo.networking.callbacks.ModelCallback;
import com.vimeo.networking.model.VideoList;
import com.vimeo.networking.model.error.VimeoError;

public class MainActivity extends AppCompatActivity {

    public static final String STAFF_PICKS_VIDEO_URI = "/channels/927/videos"; // 927 == staffpicks
    public static final String SEARCH_VIDEO_URI = "videos?query=";
    private retrofit2.Call<Object> currentCall;

    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vimeo);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //The progressbar will be displayed while Videos retrieved from Vimeo.com server
        progressBar = (ProgressBar)findViewById(R.id.progress_indicator);

        SearchView mSearchView = (SearchView) findViewById(R.id.searchview);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                //we want to send all the queries to to server in order to perform new query
                if (query.length() > 0){ //User inserted text to search bar
                    setVimeoList(SEARCH_VIDEO_URI + query);
                } else {
                    setVimeoList(STAFF_PICKS_VIDEO_URI);
                }
                return false;
            }
        });
        authenticateWithClientCredentials(getString(R.string.client_id), getString(R.string.client_secret));

    }

    /**
     * Register our app against Vimeo.com server
     * @param clientId client Id, taken from Vimeo.com site
     * @param clientSecret client Secret, from Vimeo.com
     */
    private void authenticateWithClientCredentials(String clientId, String clientSecret) {
        Configuration.Builder configBuilder =
                new Configuration.Builder(
                        clientId,
                        clientSecret,
                        "public private",
                        null,
                        new AndroidGsonDeserializer()
                );
        VimeoClient.initialize(configBuilder.build());
        VimeoClient.getInstance().authorizeWithClientCredentialsGrant(new AuthCallback() {
            @Override
            public void success() {
                setVimeoList(STAFF_PICKS_VIDEO_URI);
            }

            @Override
            public void failure(VimeoError error) {
                String errorMessage = error.getDeveloperMessage();
                Log.d("TAG", errorMessage);
            }
        });

    }

    /**
     * Get video list matched to the query and send it to the recycler adapter
     * @param query query to fetch from Viemo server
     */
    public void setVimeoList(String query){
        progressBar.setVisibility(View.VISIBLE); //display progressbar while waiting to server response
        setRecyclerView(null);
        if (currentCall != null) { //we will cancel all previous calls to the server
            currentCall.cancel();
        }
        currentCall = VimeoClient.getInstance().fetchNetworkContent(query, new ModelCallback<VideoList>(VideoList.class) {
            @Override
            public void success(VideoList videoList) {
                currentCall = null;
                if (videoList != null && videoList.data != null && !videoList.data.isEmpty()) {
                    progressBar.setVisibility(View.GONE);
                    setRecyclerView(videoList);
                }
            }

            @Override
            public void failure(VimeoError error) {
                currentCall = null;
                String errorMessage = error.getDeveloperMessage();
                Log.d("TAG", "failure:" + errorMessage);
            }
        });
    }

    private void setRecyclerView(@Nullable VideoList videoList){
        RecyclerAdapterVimeo adapterVimeo = (RecyclerAdapterVimeo) recyclerView.getAdapter();
        if (adapterVimeo != null) {
            adapterVimeo.setVideoList(videoList);
        } else {
            recyclerView.setAdapter(new RecyclerAdapterVimeo(MainActivity.this, videoList));
        }
    }
}
