RECLAIM THE ADAPTER
- THE WAY THE RECYCLERVIEW IS MEANT TO BE USED!

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

Create an adapter delegate (note the @ReclaimAdapterDelegate annotation):

	@ReclaimAdapterDelegate(KeyValueAdapterDelegate.KeyValueCell2.class)
	public class KeyValueAdapterDelegate extends AdapterDelegate {

	    public KeyValueAdapterDelegate() {}

	    public static class KeyValueCell2 implements DisplayableCell {
	        public String key;
	        public String value;

	        public KeyValueCell2(String key, String value) {
	            this.key = key;
	            this.value = value;
	        }
	    }

	    static class ViewHolder extends AdapterDelegate.ViewHolderDelegate {
	        TextView key;
	        TextView value;

	        public ViewHolder(View itemView) {
	            super(itemView);
	            key = (TextView) itemView.findViewById(R.id.left);
	            value = (TextView) itemView.findViewById(R.id.right);
	        }

	        @Override
	        public boolean needsItemDecoration() {
	            return true;
	        }
	    }

	    @NonNull
	    @Override
	    public ViewHolderDelegate onCreateViewHolder(LayoutInflater inflater, ViewGroup parent) {
	        return new ViewHolder(inflater.inflate(R.layout.adapter_delegate_key_value_view, parent, false));
	    }

	    @Override
	    public void onBindViewHolder(@NonNull DisplayableCell item, @NonNull RecyclerView.ViewHolder holder, int position) {
	        ViewHolder vh = (ViewHolder) holder;
	        KeyValueCell2 cell = (KeyValueCell2) item;

	        vh.key.setText(cell.key);
	        vh.value.setText(cell.value);
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

YOU ARE GOOD TO GO!