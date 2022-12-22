package projekt.cloud.piece.pic.ui.comicDetail

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.OnClickListener
import android.view.View.VISIBLE
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.MenuProvider
import androidx.core.view.doOnPreDraw
import androidx.core.view.get
import androidx.core.view.marginBottom
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.core.widget.NestedScrollView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle.State
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.card.MaterialCardView
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.Callback
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialSharedAxis
import kotlin.math.abs
import projekt.cloud.piece.pic.ComicDetail
import projekt.cloud.piece.pic.R
import projekt.cloud.piece.pic.api.ApiComics.EpisodeResponseBody.Data.Episode
import projekt.cloud.piece.pic.base.BaseAuthFragment
import projekt.cloud.piece.pic.databinding.FragmentComicDetailBinding
import projekt.cloud.piece.pic.util.CodeBook.AUTH_CODE_ERROR_ACCOUNT_INVALID
import projekt.cloud.piece.pic.util.CodeBook.AUTH_CODE_ERROR_CONNECTION
import projekt.cloud.piece.pic.util.CodeBook.AUTH_CODE_ERROR_NO_ACCOUNT
import projekt.cloud.piece.pic.util.CodeBook.AUTH_CODE_SUCCESS
import projekt.cloud.piece.pic.util.CodeBook.COMIC_DETAIL_CODE_ERROR_CONNECTION
import projekt.cloud.piece.pic.util.CodeBook.COMIC_DETAIL_CODE_ERROR_REJECTED
import projekt.cloud.piece.pic.util.CodeBook.COMIC_DETAIL_CODE_ERROR_UNKNOWN_ID
import projekt.cloud.piece.pic.util.CodeBook.COMIC_DETAIL_CODE_PART_SUCCESS
import projekt.cloud.piece.pic.util.CodeBook.COMIC_DETAIL_CODE_SUCCESS
import projekt.cloud.piece.pic.util.DisplayUtil.deviceBounds
import projekt.cloud.piece.pic.util.FragmentUtil.setSupportActionBar
import projekt.cloud.piece.pic.util.NestedScrollViewUtil.isScrollable
import projekt.cloud.piece.pic.util.RecyclerViewUtil.adapterAs
import projekt.cloud.piece.pic.util.StorageUtil.Account

class ComicDetailFragment: BaseAuthFragment<FragmentComicDetailBinding>(),
                           OnClickListener,
                           OnNavigationItemSelectedListener {
    
    private val drawerLayout: DrawerLayout
        get() = binding.root
    private val navigationView: NavigationView
        get() = binding.navigationView
    private val appBarLayout: AppBarLayout
        get() = binding.appBarLayout
    private val collapsingToolbarLayout: CollapsingToolbarLayout
        get() = binding.collapsingToolbarLayout
    private val toolbar: MaterialToolbar
        get() = binding.materialToolbar
    private val bottomAppBar: BottomAppBar
        get() = binding.bottomAppBar
    private val floatingActionButton: FloatingActionButton
        get() = binding.floatingActionButton
    private val tagGroup: ChipGroup
        get() = binding.chipGroup
    private val creator: MaterialCardView
        get() = binding.materialCardView
    private val creatorDetailIndicator: MaterialCheckBox
        get() = binding.materialCheckBox
    private val creatorDetail
        get() = binding.linearLayoutCompat
    private val nestedScrollView: NestedScrollView
        get() = binding.nestedScrollView
    private val recyclerView: RecyclerView
        get() = binding.recyclerView

    private val comicDetail: ComicDetail by activityViewModels()
    private val docList: ArrayList<Episode.Doc>
        get() = comicDetail.docList
    
    private lateinit var navController: NavController
    
    private var clearComicData = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController()
        
        sharedElementEnterTransition = MaterialContainerTransform()
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
    }
    
    override fun setViewModels(binding: FragmentComicDetailBinding) {
        binding.applicationConfigs = applicationConfigs
        binding.comicDetail = comicDetail
    }
    
    override fun setUpActionBar() {
        setSupportActionBar(bottomAppBar)
        toolbar.setupWithNavController(navController)
        bottomAppBar.setNavigationOnClickListener { drawerLayout.open() }
    }
    
    override fun setUpViews() {
        postponeEnterTransition()
        
        collapsingToolbarLayout.updateLayoutParams<AppBarLayout.LayoutParams> {
            height = requireContext().deviceBounds.width() * 4 / 3
        }
    
        val fabMarginBottom = floatingActionButton.marginBottom
        applicationConfigs.windowInsetBottom.observe(viewLifecycleOwner) {
            floatingActionButton.updateLayoutParams<CoordinatorLayout.LayoutParams> {
                updateMargins(bottom = fabMarginBottom + it)
            }
        }
        floatingActionButton.setOnClickListener(this)
    
        nestedScrollView.setOnScrollChangeListener { v: NestedScrollView, _, scrollY, _, _ ->
            when {
                scrollY >= v[0].measuredHeight - v.measuredHeight -> {
                    if (!bottomAppBar.isScrolledDown) {
                        bottomAppBar.performHide()
                    }
                }
                else -> {
                    if (!bottomAppBar.isScrolledUp) {
                        bottomAppBar.performShow()
                    }
                }
            }
        }
        comicDetail.comic.observe(viewLifecycleOwner) {
            it?.tags?.forEach { tag ->
                tagGroup.addView(
                    Chip(requireContext()).apply {
                        text = tag
                        isCloseIconVisible = false
                        setOnClickListener {}
                    }
                )
            }
        }
        creator.setOnClickListener(this)
        requireActivity().addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_comic_detail, menu)
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.menu_likes -> {}
                    R.id.menu_star -> {}
                }
                return true
            }
        }, viewLifecycleOwner, State.CREATED)
    
        recyclerView.adapter = RecyclerViewAdapter(docList) { doc, view ->
            launchReadComic(doc.order)
        }
        recyclerView.doOnPreDraw { startPostponedEnterTransition() }
    
        appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            when {
                abs(verticalOffset) >= appBarLayout.totalScrollRange -> {
                    if (!nestedScrollView.isScrollable && !bottomAppBar.isScrolledDown) {
                        bottomAppBar.performHide()
                    }
                }
                else -> {
                    if (!nestedScrollView.isScrollable && !bottomAppBar.isScrolledUp) {
                        bottomAppBar.performShow()
                    }
                }
            }
        }
        
        navigationView.setNavigationItemSelectedListener(this)
    }
    
    override val containerTransitionName: String?
        get() = args.getString(getString(R.string.comic_detail_transition))

    override fun onAuthComplete(code: Int, codeMessage: String?, account: Account?) {
        super.onAuthComplete(code, codeMessage, account)
        if (code != AUTH_CODE_SUCCESS) {
            when (code) {
                AUTH_CODE_ERROR_NO_ACCOUNT -> {
                    sendSnack(getString(R.string.list_snack_login_no_account))
                }
                AUTH_CODE_ERROR_ACCOUNT_INVALID -> {
                    sendSnack(getString(R.string.list_snack_login_invalid_account), resId = R.string.home_snack_action_retry) {
                        applicationConfigs.account.value?.let { requireAuth(it) }
                    }
                }
                AUTH_CODE_ERROR_CONNECTION -> {
                    sendSnack(getString(R.string.comic_detail_snack_login_connection_failed, codeMessage), resId = R.string.home_snack_action_retry) {
                        applicationConfigs.account.value?.let { requireAuth(it) }
                    }
                }
            }
        }
        comicDetail.taskReceipt.observe(viewLifecycleOwner) {
            it?.let { receiveReceipt(it.code, it.message) }
        }
    }
    
    private fun receiveReceipt(code: Int, message: String?) {
        if (isAuthSuccess) {
            when (code) {
                COMIC_DETAIL_CODE_PART_SUCCESS -> {
                    recyclerView.adapterAs<RecyclerViewAdapter>().notifyDataUpdated()
                }
        
                COMIC_DETAIL_CODE_SUCCESS -> {
                    /** Comic detail obtain success **/
                }
        
                COMIC_DETAIL_CODE_ERROR_CONNECTION -> {
                    sendSnack(getString(R.string.comic_detail_snack_request_connection_failed, message), resId = R.string.comic_detail_request_action_retry) {
                        comicDetail.retryRequestComic(token)
                    }
                }
        
                COMIC_DETAIL_CODE_ERROR_REJECTED -> {
                    sendSnack(getString(R.string.comic_detail_snack_request_server_rejected, message), resId = R.string.comic_detail_request_action_retry) {
                        comicDetail.retryRequestComic(token)
                    }
                }
                
                COMIC_DETAIL_CODE_ERROR_UNKNOWN_ID -> {
                    makeSnack(getString(R.string.comic_detail_snack_request_unknown_id), LENGTH_SHORT, null, null)
                        .addCallback(object: Callback() {
                            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                                navController.navigateUp()
                            }
                        })
                        .show()
                }
        
                else -> {
                    sendSnack(getString(R.string.comic_detail_snack_request_unknown_code, code, message), resId = R.string.comic_detail_request_action_retry) {
                        comicDetail.retryRequestComic(token)
                    }
                }
            }
        }
    }
    
    private fun launchReadComic(order: Int) {
        clearComicData = false
        navController.navigate(
            ComicDetailFragmentDirections.toReadFragment(order)
        )
    }
    
    override fun onDestroyView() {
        if (clearComicData) {
            comicDetail.clear(viewLifecycleOwner)
        }
        super.onDestroyView()
    }
    
    override fun onClick(v: View?) {
        when (v) {
            creator -> {
                creatorDetail.visibility = when (creatorDetail.visibility) {
                    VISIBLE -> GONE
                    else -> VISIBLE
                }
                creatorDetailIndicator.isChecked = creatorDetail.visibility == VISIBLE
            }
            floatingActionButton -> {
                if (docList.isNotEmpty()) {
                    launchReadComic(1)
                }
            }
        }
    }
    
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_comment -> {
                clearComicData = false
                navController.navigate(ComicDetailFragmentDirections.toCommentFragment())
            }
        }
        return true
    }
    
}