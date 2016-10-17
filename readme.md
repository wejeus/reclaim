RECLAIM THE ADAPTER

Add a local Maven repo by:

	allprojects {
	    repositories {
	        jcenter()
	        flatDir {
	            dirs 'libs'
	        }
	    }
	}

Add dependencies to your project:

	compile(name:'reclaim-android-release', ext:'aar')
	compile(name:'reclaim-annotations', ext:'jar')
	annotationProcessor(name:'reclaim-compiler', ext:'jar')

You must init the manager in your application class

	public class ApplicationDelegate extends Application {

	    @Override
	    public void onCreate() {
	        super.onCreate();

	        AdapterDelegatesManager.init();
	    }
	}

Use in your view:

	public class MainActivity extends Activity {

	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        ...
	        RecyclerView adapterView = (RecyclerView) rootView.findViewById(R.id.adapter);
	        adapterView.setLayoutManager(new LinearLayoutManager(getActivity()));
	        RecyclerAdapter adapter = new RecyclerAdapter(getActivity());
	        adapterView.setAdapter(adapter);
	        adapter.add(new KeyValueAdapterDelegate.KeyValueCell2(r.name, r.createdAt));
	        ...
	    }
	}