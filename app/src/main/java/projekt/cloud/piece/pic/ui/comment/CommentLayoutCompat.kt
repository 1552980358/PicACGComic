package projekt.cloud.piece.pic.ui.comment

import android.graphics.drawable.Drawable
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.appbar.MaterialToolbar
import projekt.cloud.piece.pic.R
import projekt.cloud.piece.pic.base.BaseRecyclerViewAdapter.BaseRecyclerViewAdapterUtil.adapterInterface
import projekt.cloud.piece.pic.base.SnackLayoutCompat
import projekt.cloud.piece.pic.databinding.FragmentCommentBinding
import projekt.cloud.piece.pic.util.AdapterInterface
import projekt.cloud.piece.pic.util.FragmentUtil.setSupportActionBar
import projekt.cloud.piece.pic.util.LayoutSizeMode
import projekt.cloud.piece.pic.util.LayoutSizeMode.COMPACT
import projekt.cloud.piece.pic.util.LayoutSizeMode.EXPANDED
import projekt.cloud.piece.pic.util.LayoutSizeMode.MEDIUM
import projekt.cloud.piece.pic.widget.DefaultedImageView

abstract class CommentLayoutCompat private constructor(
    protected val binding: FragmentCommentBinding
): SnackLayoutCompat(), AdapterInterface {
    
    companion object CommentLayoutCompatUtil {
        @JvmStatic
        fun FragmentCommentBinding.getLayoutCompat(layoutSizeMode: LayoutSizeMode) = when (layoutSizeMode) {
            COMPACT -> CommentLayoutCompatImpl(this)
            MEDIUM -> CommentLayoutCompatImpl(this)
            EXPANDED -> CommentLayoutCompatW1240Impl(this)
        }
    }
    
    override val snackContainer: View
        get() = binding.coordinatorLayout
    
    private val recyclerView: RecyclerView
        get() = binding.recyclerView
    
    private val defaultedImageView: DefaultedImageView
        get() = binding.defaultedImageView
    
    private val toolbar: MaterialToolbar
        get() = binding.materialToolbar
    
    fun setupAvatar(fragment: Fragment, commentViewModel: CommentViewModel) {
        commentViewModel.comment.observe(fragment.viewLifecycleOwner) { comment ->
            when (val avatar = comment?.user?.avatar) {
                null -> {
                    defaultedImageView.switchToDefault()
                }
                else -> {
                    Glide.with(fragment)
                        .load(avatar.getUrl())
                        .listener(
                            object: RequestListener<Drawable> {
                                override fun onLoadFailed(
                                    e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean
                                ): Boolean {
                                    defaultedImageView.switchToDefault()
                                    return false
                                }
                                override fun onResourceReady(
                                    resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean
                                ) = false
                            }
                        )
                        .into(defaultedImageView)
                }
            }
        }
    }
    
    fun setupRecyclerView(fragment: Fragment, commentViewModel: CommentViewModel) {
        recyclerView.adapter = RecyclerViewAdapter(commentViewModel.commentList, fragment)
    }
    
    fun setupActionBar(fragment: Fragment) {
        fragment.setSupportActionBar(toolbar)
        fragment.requireActivity().addMenuProvider(
            object: MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menu.clear()
                    menuInflater.inflate(R.menu.menu_comment, menu)
                }
                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    return false
                }
            }
        )
    }
    
    override fun notifyUpdate() {
        recyclerView.adapterInterface.notifyUpdate()
    }
    
    private class CommentLayoutCompatImpl(binding: FragmentCommentBinding): CommentLayoutCompat(binding)
    
    private class CommentLayoutCompatW600dpImpl(binding: FragmentCommentBinding): CommentLayoutCompat(binding)
    
    private class CommentLayoutCompatW1240Impl(binding: FragmentCommentBinding): CommentLayoutCompat(binding)
    
}