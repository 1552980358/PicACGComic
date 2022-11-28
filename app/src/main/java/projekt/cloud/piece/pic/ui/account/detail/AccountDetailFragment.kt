package projekt.cloud.piece.pic.ui.account.detail

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.android.material.appbar.MaterialToolbar
import kotlin.math.abs
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import projekt.cloud.piece.pic.R
import projekt.cloud.piece.pic.api.ApiUser.ProfileResponseBody
import projekt.cloud.piece.pic.api.ApiUser.userProfile
import projekt.cloud.piece.pic.api.CommonBody.bitmap
import projekt.cloud.piece.pic.base.BaseFragment
import projekt.cloud.piece.pic.databinding.FragmentAccountDetailBinding
import projekt.cloud.piece.pic.util.CircularCroppedDrawable
import projekt.cloud.piece.pic.util.CoroutineUtil.io
import projekt.cloud.piece.pic.util.HttpUtil.httpGet

class AccountDetailFragment: BaseFragment() {

    class AccountDetail: ViewModel() {

        fun receiveToken(token: String) {
            viewModelScope.launch {
                val profileResponseBody = withContext(io) {
                    userProfile(token)?.body?.string()?.let {
                        Json.decodeFromString<ProfileResponseBody>(it)
                    }
                }
                _profile.value = profileResponseBody?.also {
                    _avatar.value = withContext(io) {
                        it.data.user.avatar.bitmap
                    }
                }
            }
        }

        private val _profile = MutableLiveData<ProfileResponseBody>()
        val profile: MutableLiveData<ProfileResponseBody>
            get() = _profile

        private val _avatar = MutableLiveData<Bitmap?>()
        val avatar: LiveData<Bitmap?>
            get() = _avatar

    }

    private val accountDetail: AccountDetail by viewModels()

    private var _binding: FragmentAccountDetailBinding? = null
    private val binding: FragmentAccountDetailBinding
        get() = _binding!!
    private val root get() = binding.root
    private val toolbar: MaterialToolbar
        get() = binding.materialToolbar
    private val appBarLayout: AppBarLayout
        get() = binding.appBarLayout
    private val recyclerView: RecyclerView
        get() = binding.recyclerView

    private val recyclerViewAdapter = RecyclerViewAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAccountDetailBinding.inflate(inflater, container, false)
        binding.accountDetail = accountDetail
        binding.lifecycleOwner = viewLifecycleOwner
        applicationConfigs.token.observe(viewLifecycleOwner) {
            it?.let { accountDetail.receiveToken(it) }
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(requireActivity() as AppCompatActivity) {
            setSupportActionBar(toolbar)
            val navController = findNavController()
            toolbar.setupWithNavController(navController)
        }

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

        var toolbarImageView: ImageView? = null
        val appBarLayoutListener = OnOffsetChangedListener { _, verticalOffset ->
            applicationConfigs.windowInsetTop.value?.let { windowInsetTop ->
                val newAlpha = when {
                    appBarLayout.height - toolbar.height - windowInsetTop - abs(verticalOffset) > 0 -> 0F
                    else -> 1F
                }
                toolbarImageView?.let { imageView ->
                    if (imageView.alpha != newAlpha) {
                        imageView.alpha = newAlpha
                    }
                }
            }
        }
        accountDetail.avatar.observe(viewLifecycleOwner) { bitmap ->
            toolbar.logo = bitmap?.let { CircularCroppedDrawable(it) }
            if (toolbarImageView == null) {
                toolbarImageView = Toolbar::class.java
                    .declaredFields
                    .find { field -> field.name == "mLogoView" }?.let { field ->
                        field.isAccessible = true
                        field.get(toolbar as Toolbar) as? ImageView
                    }
                toolbarImageView?.alpha = 0F
            }
            appBarLayout.addOnOffsetChangedListener(appBarLayoutListener)
        }
    }

}