/*   Copyright 2004 The Apache Software Foundation
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.xmlbeans.impl.jam.editable.impl.ref;

import org.apache.xmlbeans.impl.jam.JClassLoader;

/**
 * <p>Provides a JClassRef implementation with contextual information it
 * needs to perform lazy type resolution.  This information is typically
 * provided by the JClass implementation.</p>
 *
 * @author Patrick Calahan <pcal@bea.com>
 */
public interface JClassRefContext {

  public String getPackageName();

  public String[] getImportSpecs();

  public JClassLoader getClassLoader();

}