package cz.kinst.jakub.viewmodelbinding;

import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;

import java.util.HashMap;


/**
 * Static Singleton class providing ViewModel instances based on their unique identifier.
 * <p>
 * The instance is either created using default constructor or if it exists - retrieved from a static in-memory
 * map storing previously created instances.
 */
public class ViewModelProvider {

	private static ViewModelProvider sInstance;

	/**
	 * HashMap storing ViewModel instances
	 */
	private final HashMap<String, BaseViewModel<? extends ViewDataBinding>> mViewModels;


	private ViewModelProvider() {
		mViewModels = new HashMap<>();
	}


	/**
	 * Static instance getter
	 *
	 * @return static {@link ViewModelProvider} instance
	 */
	public static ViewModelProvider getInstance() {
		if(sInstance == null)
			sInstance = new ViewModelProvider();
		return sInstance;
	}


	/**
	 * Remove a specific ViewModel from static HashMap.
	 * Call this as soon as you are sure the ViewModel won't be used anymore
	 *
	 * @param viewModelId Unique ViewModel ID used to store the ViewModel instance
	 */
	public synchronized void removeViewModel(String viewModelId) {
		mViewModels.remove(viewModelId);
	}


	/**
	 * Get an instance of specified ViewModel based on its unique ID. The instance will be either restored from an
	 * in-memory map or created using the default constructor
	 *
	 * @param viewModelId    ViewModel ID
	 * @param viewModelClass ViewModel class
	 * @return ViewModel inside a wrapper containing a flag indicating if the instance was created or restored
	 */
	@SuppressWarnings("unchecked")
	@NonNull
	public synchronized ViewModelWrapper getViewModel(String viewModelId, @NonNull Class<? extends BaseViewModel> viewModelClass) {
		// try to get the instance from in-memory map
		BaseViewModel instance = mViewModels.get(viewModelId);
		if(instance != null)
			return new ViewModelWrapper(instance, false);

		// if it doesn't exist, use the default constructor to create new instance
		try {
			instance = viewModelClass.newInstance();
			instance.setViewModelId(viewModelId);
			mViewModels.put(viewModelId, instance);
			return new ViewModelWrapper(instance, true);
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		}
	}


	/**
	 * Wrapper around ViewModel instance bearing additional information
	 * such as a flag indicating if the instance was created or restored
	 */
	public static class ViewModelWrapper {
		@NonNull
		private final BaseViewModel mViewModel;
		private final boolean mWasCreated;


		private ViewModelWrapper(@NonNull BaseViewModel viewModel, boolean wasCreated) {
			this.mViewModel = viewModel;
			this.mWasCreated = wasCreated;
		}


		/**
		 * Provides the actual wrapped ViewModel instance
		 *
		 * @return ViewModel instance
		 */
		@NonNull
		public BaseViewModel getViewModel() {
			return mViewModel;
		}


		/**
		 * Returns true if the ViewModel was instantiated and not found in the static map
		 *
		 * @return true if the ViewModel was instantiated and not found in the static map
		 */
		public boolean wasCreated() {
			return mWasCreated;
		}
	}
}
