package projekt.cloud.piece.pic.ui.commenting

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.core.view.WindowCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import projekt.cloud.piece.pic.MainViewModel
import projekt.cloud.piece.pic.R
import projekt.cloud.piece.pic.databinding.DialogFragmentCommentingBinding
import projekt.cloud.piece.pic.ui.commenting.CommentingLayoutCompat.CommentingLayoutCompatUtil.getLayoutCompat
import projekt.cloud.piece.pic.util.LayoutSizeMode
import projekt.cloud.piece.pic.util.LayoutSizeMode.COMPACT
import projekt.cloud.piece.pic.util.LayoutSizeMode.LayoutSizeModeUtil.getLayoutSize

class Commenting: DialogFragment() {
    
    private var _binding: DialogFragmentCommentingBinding? = null
    private val binding: DialogFragmentCommentingBinding
        get() = _binding!!
    
    private lateinit var layoutSizeMode: LayoutSizeMode
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_Pic_Commenting)
        layoutSizeMode = requireActivity().getLayoutSize()
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogFragmentCommentingBinding.inflate(inflater, container, false)
        binding.mainViewModel = activityViewModels<MainViewModel>().value
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.getLayoutCompat(requireActivity().getLayoutSize()).let { layoutCompat ->
            layoutCompat.setupActionBar(this)
            layoutCompat.setupWithArgument(this, requireArguments())
            layoutCompat.setupMenu(this)
        }
    }
    
    override fun onStart() {
        super.onStart()
        if (layoutSizeMode == COMPACT) {
            dialog?.window?.let {
                WindowCompat.setDecorFitsSystemWindows(it, false)
            }
        }
    }
    
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
    
}