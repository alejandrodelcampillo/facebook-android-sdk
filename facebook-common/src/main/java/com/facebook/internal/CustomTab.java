/*
 * Copyright (c) 2014-present, Facebook, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by Facebook.
 *
 * As with any software that integrates with the Facebook platform, your use of
 * this software is subject to the Facebook Developer Principles and Policies
 * [http://developers.facebook.com/policy/]. This copyright notice shall be
 * included in all copies or substantial portions of the software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.facebook.internal;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.browser.customtabs.CustomTabsSession;
import com.facebook.FacebookSdk;
import com.facebook.internal.instrument.crashshield.AutoHandleExceptions;
import com.facebook.internal.qualityvalidation.Excuse;
import com.facebook.internal.qualityvalidation.ExcusesForDesignViolations;
import com.facebook.login.CustomTabPrefetchHelper;

@AutoHandleExceptions
@ExcusesForDesignViolations(@Excuse(type = "MISSING_UNIT_TEST", reason = "Legacy"))
public class CustomTab {

  protected Uri uri;

  public CustomTab(String action, Bundle parameters) {
    if (parameters == null) {
      parameters = new Bundle();
    }
    uri = getURIForAction(action, parameters);
  }

  public static Uri getURIForAction(String action, Bundle parameters) {
    return Utility.buildUri(
        ServerProtocol.getDialogAuthority(),
        FacebookSdk.getGraphApiVersion() + "/" + ServerProtocol.DIALOG_PATH + action,
        parameters);
  }

  public boolean openCustomTab(Activity activity, String packageName) {
    CustomTabsSession session = CustomTabPrefetchHelper.getPreparedSessionOnce();
    CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder(session).build();
    customTabsIntent.intent.setPackage(packageName);
    try {
      customTabsIntent.launchUrl(activity, uri);
    } catch (ActivityNotFoundException e) {
      return false;
    }

    return true;
  }
}
