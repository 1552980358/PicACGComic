package projekt.cloud.piece.pic.base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.StringRes
import androidx.annotation.UiThread
import androidx.core.os.bundleOf
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.clearFragmentResultListener
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import java.lang.reflect.ParameterizedType
import kotlinx.coroutines.withContext
import projekt.cloud.piece.pic.ApplicationConfigs
import projekt.cloud.piece.pic.R
import projekt.cloud.piece.pic.api.ApiAuth
import projekt.cloud.piece.pic.api.ApiAuth.signIn
import projekt.cloud.piece.pic.util.CodeBook.AUTH_CODE_ERROR_ACCOUNT_INVALID
import projekt.cloud.piece.pic.util.CodeBook.AUTH_CODE_ERROR_CONNECTION
import projekt.cloud.piece.pic.util.CodeBook.AUTH_CODE_ERROR_NO_ACCOUNT
import projekt.cloud.piece.pic.util.CodeBook.AUTH_CODE_SUCCESS
import projekt.cloud.piece.pic.util.CodeBook.HTTP_REQUEST_CODE_SUCCESS
import projekt.cloud.piece.pic.util.CoroutineUtil.io
import projekt.cloud.piece.pic.util.CoroutineUtil.ui
import projekt.cloud.piece.pic.util.HttpUtil.HTTP_RESPONSE_CODE_SUCCESS
import projekt.cloud.piece.pic.util.ResponseUtil.decodeJson
import projekt.cloud.piece.pic.util.SnackUtil.snack
import projekt.cloud.piece.pic.util.StorageUtil.Account

abstract class BaseFragment<VB: ViewBinding>: Fragment() {
    
    private companion object {
        const val VIEW_BINDING_INFLATE_METHOD_NAME = "inflate"
    }
    
    protected val applicationConfigs: ApplicationConfigs by activityViewModels()
    
    private var needInitialSetUp = false
    
    private var _binding: VB? = null
    protected val binding: VB
        get() = _binding!!
    
    protected val args by lazy { requireArguments() }
    
    private var _token: String? = null
    protected val token: String
        get() = _token!!
    protected var isAuthComplete = false
    protected val isAuthSuccess: Boolean
        get() = isAuthComplete && _token != null
    
    @Suppress("UNCHECKED_CAST")
    private val viewBindingClass =
        ((this::class.java.genericSuperclass as ParameterizedType)
            .actualTypeArguments.first() as Class<VB>)
    
    private val viewBindingInflateMethod =
        viewBindingClass.getDeclaredMethod(
            VIEW_BINDING_INFLATE_METHOD_NAME,
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        )
    
    @Suppress("UNCHECKED_CAST")
    protected fun inflateViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        viewBindingInflateMethod.invoke(null, inflater, container, false) as VB
    
    protected open fun setViewModels(binding: VB) = Unit
    
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(this::class.java.simpleName, "onCreate")
        super.onCreate(savedInstanceState)
        
        requireActivity().onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (onBackPressed()) {
                    isEnabled = false
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        })
        
        val requestKey = getString(R.string.fragment_message)
        setFragmentResultListener(requestKey) { _, bundle ->
            clearFragmentResultListener(getString(R.string.fragment_message))
            
            when (bundle.getString(getString(R.string.fragment_message_sender))) {
                this::class.simpleName -> bundle.getString(getString(R.string.fragment_message_message))?.let {
                    onMessageReceived(it)
                }
                else -> setFragmentResult(requestKey, bundle)
            }
        }
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.i(this::class.java.simpleName, "onCreateView")
        _binding = inflateViewBinding(inflater, container)
        val binding = binding
        if (binding is ViewDataBinding) {
            binding.lifecycleOwner = this
            setViewModels(binding)
        }
        return binding.root.apply { containerTransitionName?.let { transitionName = it } }
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.i(this::class.java.simpleName, "onViewCreated")
        setUpToolbar()
        setUpViews()
        applicationConfigs.account.observe(viewLifecycleOwner) {
            if (it == null) {
                return@observe onAuthComplete(AUTH_CODE_ERROR_NO_ACCOUNT, null, null)
            }
            requireAuth(it)
        }
    }
    
    protected fun requireAuth(account: Account) {
        lifecycleScope.ui {
            var token = account.token
            if (token != null) {
                _token = token
                return@ui onAuthComplete(AUTH_CODE_SUCCESS, null, account)
            }
            
            val httpResponse = withContext(io) {
                signIn(account.account, account.password)
            }
            
            // Http code
            val response = httpResponse.response
            if (response == null || httpResponse.code != HTTP_REQUEST_CODE_SUCCESS) {
                return@ui onAuthComplete(AUTH_CODE_ERROR_CONNECTION, httpResponse.message, account)
            }
            // Invalid response code
            if (response.code != HTTP_RESPONSE_CODE_SUCCESS) {
                return@ui onAuthComplete(AUTH_CODE_ERROR_ACCOUNT_INVALID, null, account)
            }
            
            token = withContext(io) {
                response.decodeJson<ApiAuth.SignInResponseBody>().token
            }
            _token = token
            account.token = token
            onAuthComplete(AUTH_CODE_SUCCESS, null, account)
        }
    }
    
    override fun onStart() {
        Log.i(this::class.java.simpleName, "onStart")
        super.onStart()
    }
    
    override fun onResume() {
        Log.i(this::class.java.simpleName, "onResume")
        super.onResume()
    }
    
    override fun onPause() {
        Log.i(this::class.simpleName, "onPause")
        super.onPause()
    }
    
    override fun onStop() {
        Log.i(this::class.simpleName, "onStop")
        super.onStop()
    }
    
    override fun onDestroy() {
        Log.i(this::class.simpleName, "onDestroyView")
        super.onDestroy()
    }
    
    override fun onDestroyView() {
        Log.i(this::class.simpleName, "onDestroyView")
        _binding = null
        super.onDestroyView()
    }
    
    protected open fun onBackPressed(): Boolean {
        return true
    }
    
    protected open fun setUpToolbar() = Unit
    
    protected abstract fun setUpViews()
    protected open val containerTransitionName: String?
        get() = null
    
    protected inline fun <reified F: Fragment> findParentAs(): F {
        var parent = requireParentFragment()
        while (parent !is F) {
            parent = parent.requireParentFragment()
        }
        return parent
    }
    
    open fun onMessageReceived(message: String) = Unit
    
    fun sendMessage(message: String) {
        val resultKey = getString(R.string.fragment_message)
        clearFragmentResultListener(resultKey)
        setFragmentResult(
            resultKey,
            bundleOf(
                getString(R.string.fragment_message_sender) to this::class.simpleName,
                getString(R.string.fragment_message_message) to message
            )
        )
    }
    
    @UiThread
    open fun onAuthComplete(code: Int, codeMessage: String?, account: Account?) {
        Log.i(this::class.java.simpleName, "onAuthComplete")
        isAuthComplete = true
    }
    
    protected open val snackAnchor: View?
        get() = null
    
    protected open fun sendSnack(message: String,
                                 length: Int = LENGTH_INDEFINITE,
                                 @StringRes resId: Int? = null,
                                 action: OnClickListener? = null) {
        makeSnack(message, length, resId, action).show()
    }
    
    protected open fun makeSnack(message: String, length: Int = LENGTH_INDEFINITE, @StringRes resId: Int?, action: OnClickListener?) =
        binding.root.snack(message, length)
            .apply { resId?.let { setAction(resId, action) } }
            .setAnchorView(snackAnchor)
    
}