package com.messi.languagehelper.util;

import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.messi.languagehelper.views.TouchableSpan;

import android.app.Activity;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class TextHandlerUtil {

	public static void handlerText(Activity context, ProgressBarCircularIndeterminate mProgressbar,
			TextView contentTv, String text) {
		SpannableString ss = new SpannableString(text);
		boolean isIn = false;
		boolean isFinish = false;
		int star = -1;
		int end = -1;
		char[] arr = text.toCharArray();
		for (int i = 0; i < arr.length; i++) {
			if ((arr[i] >= 65 && arr[i] <= 90) || (arr[i] >= 97 && arr[i] <= 125) || (arr[i] == 39)
					|| (arr[i] == 8217)) {
				if (!isIn) {
					isIn = true;
					star = i;
				}
			} else {
				if (isIn && !isFinish) {
					isFinish = true;
					end = i;
				}
			}
			if (isIn && isFinish) {
				isIn = false;
				isFinish = false;
				final String word = text.substring(star, end);
				TouchableSpan clickableSpan = new TouchableSpan(context, mProgressbar, word);
				ss.setSpan(clickableSpan, star, end,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				star = -1;
				end = -1;
			}
		}
		contentTv.setText(ss);
		contentTv.setMovementMethod(LinkMovementMethod.getInstance());
		contentTv.setHighlightColor(Color.TRANSPARENT);
	}

	// public static void count(String str) {
	// int count = 0;
	// Scanner s = new Scanner(text).useDelimiter(" |,|\\?|\\.");
	// while (s.hasNext()) {
	// final String word = s.next();
	// if (StringUtils.isEnglish(word)) {
	// ClickableSpan clickableSpan = new ClickableSpan() {
	// @Override
	// public void onClick(View textView) {
	// LogUtil.DefalutLog(word);
	// }
	// };
	// ss.setSpan(clickableSpan, text.indexOf(word + ""), text.indexOf(word) +
	// word.length(),
	// Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
	// LogUtil.DefalutLog(word);
	// LogUtil.DefalutLog(String.valueOf(text.indexOf(word)));
	// count++;
	// }
	// }
	// }

}
