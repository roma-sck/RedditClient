package com.example.redditclient.mvp

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface MainView: MvpView {

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showError(throwable: Throwable)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showLoader()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun hideLoader()

    fun render(viewModel: PostsViewModel)
}