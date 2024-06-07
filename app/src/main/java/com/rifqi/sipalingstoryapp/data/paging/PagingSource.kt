package com.rifqi.sipalingstoryapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.rifqi.sipalingstoryapp.data.api.ApiService
import com.rifqi.sipalingstoryapp.data.model.Story
import java.io.IOException

class PagingSource (
    private val apiService: ApiService
): PagingSource<Int, Story>() {
    override fun getRefreshKey(state: PagingState<Int, Story>): Int? {
        return state.anchorPosition?.let { ancPos->
            val ancPage = state.closestPageToPosition(ancPos)
            ancPage?.prevKey?.plus(1) ?: ancPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> {
        return try {
            val page = params.key ?: 1
            val response = apiService.getStories(page, params.loadSize, null)
            val data =  response.listStory

            LoadResult.Page(
                data = data,
                prevKey = if (page == 1) null else page -1,
                nextKey = if (data.isEmpty()) null else page +1
            )

        } catch (ioe: IOException) {
            return LoadResult.Error(ioe)
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }
}