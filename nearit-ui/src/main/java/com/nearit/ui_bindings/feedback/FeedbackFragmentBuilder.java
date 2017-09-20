package com.nearit.ui_bindings.feedback;

import android.content.Context;

import it.near.sdk.reactions.feedbackplugin.model.Feedback;

/**
 * Created by Federico Boschini on 06/09/17.
 */

public class FeedbackFragmentBuilder {
    private Context mContext;
    private boolean mHideTextResponse;
    private Feedback mFeedback;

    public FeedbackFragmentBuilder(Context context, Feedback feedback) {
        mContext = context;
        mFeedback = feedback;
    }

    /**
     * Sets no text response
     */
    public FeedbackFragmentBuilder hideTextResponse() {
        mHideTextResponse = true;
        return this;
    }

    public NearItFeedbackFragment build() {
        return NearItFeedbackFragment.newInstance(mFeedback, getParams());
    }

    private FeedbackRequestExtras getParams() {
        return new FeedbackRequestExtras(
                mHideTextResponse);
    }

}
