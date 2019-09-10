package com.cymbit.plastr.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cymbit.plastr.R
import com.cymbit.plastr.adapter.ExploreAdapter
import com.cymbit.plastr.helpers.Firebase
import com.cymbit.plastr.service.RedditFetch
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_explore.*

class FavoriteFragment : Fragment() {
    private lateinit var mGridAdapter: ExploreAdapter
    private val fb: Firebase = Firebase()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db.collection("favorites").whereEqualTo("user", fb.auth.currentUser!!.uid).addSnapshotListener { document, e  ->
            if (e != null) return@addSnapshotListener
            if (document != null ) {
                val favorites = document.toObjects(RedditFetch.RedditChildrenData::class.java)
                if (!this::mGridAdapter.isInitialized) {
                    println(favorites.size)
                    initGridView(favorites)
                    mGridAdapter.notifyDataSetChanged()
                } else {
                    mGridAdapter.clear()
                    mGridAdapter.add(favorites)
                }
            }
        }
    }

    private fun initGridView(favorites: MutableList<RedditFetch.RedditChildrenData>) {
        rvItems.layoutManager = GridLayoutManager(context, 2)
        mGridAdapter = view?.let { ExploreAdapter(favorites, it) }!!
        rvItems.adapter = mGridAdapter
        mGridAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                checkEmpty()
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                checkEmpty()
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                super.onItemRangeRemoved(positionStart, itemCount)
                checkEmpty()
            }

            fun checkEmpty() {
                empty_view.visibility =
                    (if (mGridAdapter.itemCount == 0) View.VISIBLE else View.GONE)
            }
        })
    }
}