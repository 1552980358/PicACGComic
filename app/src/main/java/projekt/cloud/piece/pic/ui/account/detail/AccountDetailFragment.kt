package projekt.cloud.piece.pic.ui.account.detail

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuProvider
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT
import kotlin.math.abs
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import projekt.cloud.piece.pic.R
import projekt.cloud.piece.pic.api.ApiUser.ProfileResponseBody
import projekt.cloud.piece.pic.api.ApiUser.userProfile
import projekt.cloud.piece.pic.base.BaseAuthFragment
import projekt.cloud.piece.pic.databinding.FragmentAccountDetailBinding
import projekt.cloud.piece.pic.util.AnimationUtil.animateAlphaTo
import projekt.cloud.piece.pic.util.CircularCroppedDrawable
import projekt.cloud.piece.pic.util.CodeBook.ACCOUNT_DETAIL_CODE_ERROR_CONNECTION
import projekt.cloud.piece.pic.util.CodeBook.ACCOUNT_DETAIL_CODE_REJECTED
import projekt.cloud.piece.pic.util.CodeBook.ACCOUNT_DETAIL_CODE_SUCCESS
import projekt.cloud.piece.pic.util.CodeBook.HTTP_REQUEST_CODE_SUCCESS
import projekt.cloud.piece.pic.util.CompleteCallback
import projekt.cloud.piece.pic.util.CoroutineUtil.io
import projekt.cloud.piece.pic.util.CoroutineUtil.ui
import projekt.cloud.piece.pic.util.FragmentUtil.addMenuProvider
import projekt.cloud.piece.pic.util.FragmentUtil.setSupportActionBar
import projekt.cloud.piece.pic.util.HttpUtil.HTTP_RESPONSE_CODE_SUCCESS
import projekt.cloud.piece.pic.util.ResponseUtil.decodeJson
import projekt.cloud.piece.pic.util.StorageUtil.Account
import projekt.cloud.piece.pic.util.StorageUtil.saveAccount

class AccountDetailFragment: BaseAuthFragment<FragmentAccountDetailBinding>() {

    class AccountDetail: ViewModel() {
        
        fun requestDetail(token: String, resources: Resources, completeCallback: CompleteCallback) {
            
            viewModelScope.ui {
                
                val httpResponse = withContext(io) {
                    userProfile(token)
                }
                
                val response = httpResponse.response
                if (httpResponse.code != HTTP_REQUEST_CODE_SUCCESS || response == null) {
                    return@ui completeCallback.invoke(ACCOUNT_DETAIL_CODE_ERROR_CONNECTION, httpResponse.message)
                }
                
                if (response.code != HTTP_RESPONSE_CODE_SUCCESS) {
                    return@ui completeCallback.invoke(ACCOUNT_DETAIL_CODE_REJECTED, null)
                }
                
                val profile = withContext(io) {
                    response.decodeJson<ProfileResponseBody>()
                }
                
                _profile.value = profile
                
                var bitmap = withContext(io) {
                    profile.data.user.avatar?.bitmap
                }
                if (bitmap == null) {
                    bitmap = withContext(io) {
                        BitmapFactory.decodeResource(resources, R.drawable.ic_round_account_circle_24)
                    }
                }
                _avatar.value = bitmap
            }
    
            completeCallback.invoke(ACCOUNT_DETAIL_CODE_SUCCESS, null)
        }

        private val _profile = MutableLiveData<ProfileResponseBody>()
        val profile: MutableLiveData<ProfileResponseBody>
            get() = _profile

        private val _avatar = MutableLiveData<Bitmap?>()
        val avatar: LiveData<Bitmap?>
            get() = _avatar

    }

    private val accountDetail: AccountDetail by viewModels()

    private val toolbar: MaterialToolbar
        get() = binding.materialToolbar
    private val appBarLayout: AppBarLayout
        get() = binding.appBarLayout
    private val recyclerView: RecyclerView
        get() = binding.recyclerView
    
    override fun setViewModels(binding: FragmentAccountDetailBinding) {
        binding.accountDetail = accountDetail
    }
    
    override fun setUpActionBar() {
        setSupportActionBar(toolbar)
        toolbar.setupWithNavController(findNavController())
    }
    
    override fun setUpViews() {
        addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.menu_account_detail, menu)
                if (menu is MenuBuilder) {
                    @Suppress("RestrictedApi")
                    menu.setOptionalIconsVisible(true)
                }
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.menu_exit -> {
                        lifecycleScope.ui {
                            runBlocking(io) {
                                requireContext().saveAccount(null)
                            }
                            /**
                             * Should Remove observer before updating
                             * Otherwise, java.lang.OutOfMemoryError thrown
                             **/
                            applicationConfigs.account.removeObservers(viewLifecycleOwner)
                            applicationConfigs.setAccount(null)
                        }
                    }
                }
                return true
            }
        })
        
        val recyclerViewAdapter = RecyclerViewAdapter()
        recyclerView.adapter = recyclerViewAdapter
        accountDetail.profile.observe(viewLifecycleOwner) {
            recyclerViewAdapter.setDetails(
                mapOf(
                    R.string.account_detail_id to it.data.user.email,
                    R.string.account_detail_title to it.data.user.title,
                    R.string.account_detail_level to it.data.user.level.toString(),
                    R.string.account_detail_birthday to it.data.user.birthday.substring(0, 10)
                )
            )
        }
        
        val toolbarLogoView = reflectToolbarLogoView()
        with(toolbarLogoView) {
            alpha = 0F
            val paddingHor = resources.getDimensionPixelSize(R.dimen.account_detail_small_avatar_spacing_hor)
            val paddingVer = resources.getDimensionPixelSize(R.dimen.account_detail_small_avatar_spacing_ver)
            updatePadding(paddingHor, paddingVer, paddingHor, paddingVer)
        }
    
        appBarLayout.addOnOffsetChangedListener { _, verticalOffset ->
            applicationConfigs.windowInsetTop.value?.let { windowInsetTop ->
                val newAlpha = when {
                    appBarLayout.height - toolbar.height - windowInsetTop - abs(verticalOffset) > 0 -> 0F
                    else -> 1F
                }
                toolbarLogoView.animateAlphaTo(newAlpha)
            }
        }
        
        accountDetail.avatar.observe(viewLifecycleOwner) { bitmap ->
            toolbar.logo = bitmap?.let { CircularCroppedDrawable(it) }
        }
    }
    
    private fun reflectToolbarLogoView(): ImageView {
        // Call for set ImageView
        Toolbar::class.java
            .getDeclaredMethod("ensureLogoView")
            .apply { isAccessible = true }
            .invoke(toolbar)
    
        return Toolbar::class.java
            .getDeclaredField("mLogoView")
            .apply { isAccessible = true }
            .get(toolbar) as ImageView
    }
    
    override fun onAuthComplete(code: Int, codeMessage: String?, account: Account?) {
        val token = account?.token ?: return applicationConfigs.setAccount(account)
        requestAccountDetail(token)
    }
    
    private fun requestAccountDetail(token: String) {
        accountDetail.requestDetail(token, resources) { c, message ->
            when (c) {
                ACCOUNT_DETAIL_CODE_SUCCESS -> { /** Account Detail Request completed **/ }
                ACCOUNT_DETAIL_CODE_ERROR_CONNECTION -> {
                    sendSnack(
                        getString(R.string.account_detail_snack_connection_failure, message),
                        LENGTH_SHORT,
                        R.string.account_detail_snack_action_retry) {
                        requestAccountDetail(token)
                    }
                }
                ACCOUNT_DETAIL_CODE_REJECTED -> {
                    sendSnack(
                        getString(R.string.account_detail_snack_server_rejected),
                        LENGTH_SHORT,
                        R.string.account_detail_snack_action_retry) {
                        requestAccountDetail(token)
                    }
                }
                else -> {
                    sendSnack(
                        getString(R.string.account_detail_snack_unknown_code, c, message),
                        LENGTH_SHORT,
                        R.string.account_detail_snack_action_retry) {
                        requestAccountDetail(token)
                    }
                }
            }
        }
    }

}