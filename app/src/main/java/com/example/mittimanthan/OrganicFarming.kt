package com.example.mittimanthan

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mittimanthan.adapters.BlogAdapter
import com.example.mittimanthan.adapters.VideoAdapter
import com.example.mittimanthan.models.BlogPost
import com.example.mittimanthan.models.VideoPost

class OrganicFarming : Fragment() {
    private lateinit var blogRecyclerView: RecyclerView
    private lateinit var videoRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_organic_farming, container, false)

        setupBlogRecyclerView(view)
        setupVideoRecyclerView(view)

        return view
    }

    private fun setupBlogRecyclerView(view: View) {
        blogRecyclerView = view.findViewById(R.id.blogRecyclerView)
        blogRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        val blogs = listOf(
            BlogPost(
                "Organic Farming Basics",
                "Learn the fundamental principles of organic farming and sustainable agriculture",
                R.drawable.blog,
                "https://sentientmedia.org/organic-farming/?gad_source=1&gclid=CjwKCAiApsm7BhBZEiwAvIu2X1x0mvIvxe5jPircqqNdhz0G2eHs_QeZry15GtrII7rRNcUa1BrurxoCbLEQAvD_BwE"
            ),
            BlogPost(
                "Natural Pest Control",
                "Effective ways to control pests without using harmful chemicals",
                R.drawable.blog,
                "https://blog.abchomeandcommercial.com/natural-pest-control-methods-for-your-home-and-garden/"
            ),
            BlogPost(
                "Soil Health Management",
                "Tips for maintaining healthy soil in organic farming",
                R.drawable.blog,
                "https://www.mosaiccoindia.com/blog/soil-health-management-practices-in-india/"
            ),

        )

        val blogAdapter = BlogAdapter(blogs) { blogPost ->
            openUrl(blogPost.url)
        }
        blogRecyclerView.adapter = blogAdapter
    }

    private fun setupVideoRecyclerView(view: View) {
        videoRecyclerView = view.findViewById(R.id.videoRecyclerView)
        videoRecyclerView.layoutManager = LinearLayoutManager(context)

        val videos = listOf(
            VideoPost(
                "Getting Started with Organic Farming",
                "Learn the basics of organic farming in this comprehensive guide",
                R.drawable.thumb,
                "https://youtu.be/IwFM5wt80cg?si=2RRdhRtmPYIbkV52",
                "2:38"
            ),
            VideoPost(
                "Natural Fertilizers Guide",
                "How to make and use natural fertilizers for your organic farm",
                R.drawable.thumb,
                "https://youtu.be/lofNYAtHYu4?si=0hNwjQncqmEuhcnl",
                "4:14"
            ),
            VideoPost(
                "Crop Rotation Techniques",
                "Master the art of crop rotation for better yields",
                R.drawable.thumb,
                "https://youtu.be/xIYsB_2_6go?si=81WbqUqr42kWiFaN",
                "3:59"
            ),
        )

        val videoAdapter = VideoAdapter(videos) { videoPost ->
            openUrl(videoPost.url)
        }
        videoRecyclerView.adapter = videoAdapter
    }

    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}