package com.andriod.movies.playvideo

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.fragment.app.DialogFragment
import com.andriod.movies.databinding.FragmentPlayVideoBinding
import com.andriod.movies.entity.Video

class DialogPlayVideo : DialogFragment() {

    private var _binding: FragmentPlayVideoBinding? = null
    private val binding: FragmentPlayVideoBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPlayVideoBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            val video = it.getParcelable<Video>(VIDEOS_EXTRA_KEY)

            if (video != null && video.link.isNotBlank()) {
                binding.videoView.apply {
                    setVideoURI(Uri.parse(video.link))
                    setOnPreparedListener {
                        val mediaController = MediaController(context)
                        setMediaController(mediaController)
                        mediaController.setAnchorView(this)
                    }
                    start()
                }
            }else{
                dismiss()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val VIDEOS_EXTRA_KEY = "videos"

        @JvmStatic
        fun newInstance(video: Video) =
            DialogPlayVideo().apply {
                arguments = Bundle().apply {
                    putParcelable(VIDEOS_EXTRA_KEY, video)
                }
            }
    }
}