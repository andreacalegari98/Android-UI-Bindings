package com.nearit.ui_bindings.feedback;

import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.nearit.ui_bindings.R;
import com.nearit.ui_bindings.feedback.views.NearItUIFeedbackButton;

import it.near.sdk.NearItManager;
import it.near.sdk.reactions.feedbackplugin.FeedbackEvent;
import it.near.sdk.reactions.feedbackplugin.model.Feedback;
import it.near.sdk.recipes.NearITEventHandler;

public class NearItFeedbackFragment extends Fragment {
    private static final String ARG_FEEDBACK = "feedback";
    private static final String ARG_EXTRAS = "extras";
    private static final String SAVED_RATING = "rating";
    private static final String SAVED_IS_BUTTON_CHECKED = "button_state";
    private static final String SAVED_IS_ALERT_VISIBLE = "alert_state";
    private static int NEAR_AUTOCLOSE_DELAY = 2000;

    private Feedback feedback;

    private boolean hideTextResponse = false;
    private boolean noSuccessIcon = false;
    private int successIconResId = 0;

    private String feedbackQuestion;
    private float userRating;
    private String userComment;

    @Nullable
    RatingBar ratingBar;
    @Nullable
    LinearLayout commentSection, ratingBarContainer;
    @Nullable
    NearItUIFeedbackButton sendButton;
    @Nullable
    TextView closeButton, errorText, feedbackQuestionTextView;
    @Nullable
    EditText commentBox;
    @Nullable
    ImageView positiveResultIcon;
    @Nullable
    TextView positiveResultMessage;

    public NearItFeedbackFragment() {
    }

    public static NearItFeedbackFragment newInstance(Feedback feedback, Parcelable extras) {
        NearItFeedbackFragment fragment = new NearItFeedbackFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_EXTRAS, extras);
        bundle.putParcelable(ARG_FEEDBACK, feedback);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        feedback = getArguments().getParcelable(ARG_FEEDBACK);
        FeedbackRequestExtras extras = getArguments().getParcelable(ARG_EXTRAS);
        if (extras != null) {
            hideTextResponse = extras.isHideTextResponse();
            successIconResId = extras.getIconResId();
            noSuccessIcon = extras.isNoSuccessIcon();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Save the fragment's state here
        if (ratingBar != null) {
            outState.putFloat(SAVED_RATING, ratingBar.getRating());
        }
        if (sendButton != null) {
            outState.putBoolean(SAVED_IS_BUTTON_CHECKED, sendButton.isChecked());
        }
        if (errorText != null) {
            outState.putBoolean(SAVED_IS_ALERT_VISIBLE, errorText.isShown());
        }

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            //Restore the fragment's state here
            if (ratingBar != null) {
                ratingBar.setRating(savedInstanceState.getFloat(SAVED_RATING));
            }
            if (sendButton != null) {
                if (savedInstanceState.getBoolean(SAVED_IS_BUTTON_CHECKED)) {
                    sendButton.setChecked();
                } else {
                    sendButton.setUnchecked();
                }
            }
            if (errorText != null) {
                if (savedInstanceState.getBoolean(SAVED_IS_ALERT_VISIBLE)) {
                    errorText.setVisibility(View.VISIBLE);
                } else {
                    errorText.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.nearit_ui_fragment_feedback, container, false);

        feedbackQuestion = feedback.question;

        ratingBarContainer = (LinearLayout) rootView.findViewById(R.id.feedback_rating_bar_container);
        ratingBar = (RatingBar) rootView.findViewById(R.id.feedback_rating);
        commentSection = (LinearLayout) rootView.findViewById(R.id.feedback_comment_section);
        errorText = (TextView) rootView.findViewById(R.id.feedback_error_alert);
        sendButton = (NearItUIFeedbackButton) rootView.findViewById(R.id.feedback_send_comment_button);
        closeButton = (TextView) rootView.findViewById(R.id.feedback_dismiss_text);
        commentBox = (EditText) rootView.findViewById(R.id.feedback_comment_box);
        feedbackQuestionTextView = (TextView) rootView.findViewById(R.id.feedback_question);

        positiveResultMessage = (TextView) rootView.findViewById(R.id.feedback_success_message);
        positiveResultIcon = (ImageView) rootView.findViewById(R.id.feedback_success_icon);

        if (successIconResId != 0 && positiveResultIcon != null) {
            positiveResultIcon.setImageResource(successIconResId);
        }

        if (feedbackQuestionTextView != null) {
            feedbackQuestionTextView.setText(feedbackQuestion);
        }

        if (commentSection != null) {
            commentSection.setVisibility(View.GONE);
        }

        if (closeButton != null) {
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });
        }

        if (ratingBar != null) {
            ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    if (rating < 1.0f) {
                        ratingBar.setRating(1.0f);
                    }

                    userRating = rating;

                    if (commentSection != null) {
                        if (!hideTextResponse) {
                            commentSection.setVisibility(View.VISIBLE);
                        } else {
                            commentSection.setVisibility(View.GONE);
                        }
                    }

                    if (sendButton != null) {
                        sendButton.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        if (sendButton != null) {
            sendButton.setUnchecked();
            sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendButton.setLoading();
                    if (userRating < 1.0f) {
                        userRating = 1.0f;
                    }
                    userComment = commentBox != null ? commentBox.getText().toString() : "";
                    NearItManager.getInstance().sendEvent(new FeedbackEvent(feedback, (int) userRating, userComment), new NearITEventHandler() {
                        @Override
                        public void onSuccess() {
                            if (sendButton != null) {
                                sendButton.setVisibility(View.GONE);
                            }
                            if (ratingBarContainer != null) {
                                ratingBarContainer.setVisibility(View.GONE);
                            }
                            if (feedbackQuestionTextView != null) {
                                feedbackQuestionTextView.setVisibility(View.GONE);
                            }
                            if (commentSection != null) {
                                commentSection.setVisibility(View.GONE);
                            }
                            if (errorText != null) {
                                errorText.setVisibility(View.GONE);
                            }
                            if (closeButton != null) {
                                closeButton.setVisibility(View.GONE);
                            }
                            if (positiveResultMessage != null) {
                                positiveResultMessage.setVisibility(View.VISIBLE);
                            }
                            if (!noSuccessIcon && positiveResultIcon != null) {
                                positiveResultIcon.setVisibility(View.VISIBLE);
                            }

                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (getActivity() != null) {
                                        getActivity().finish();
                                    }
                                }
                            }, NEAR_AUTOCLOSE_DELAY);
                        }

                        @Override
                        public void onFail(int i, String s) {
                            if (errorText != null) {
                                errorText.setVisibility(View.VISIBLE);
                            }
                            sendButton.setChecked();
                        }
                    });
                }
            });
        }


        return rootView;
    }
}