/*
* The Apache Software License, Version 1.1
*
*
* Copyright (c) 2003 The Apache Software Foundation.  All rights
* reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions
* are met:
*
* 1. Redistributions of source code must retain the above copyright
*    notice, this list of conditions and the following disclaimer.
*
* 2. Redistributions in binary form must reproduce the above copyright
*    notice, this list of conditions and the following disclaimer in
*    the documentation and/or other materials provided with the
*    distribution.
*
* 3. The end-user documentation included with the redistribution,
*    if any, must include the following acknowledgment:
*       "This product includes software developed by the
*        Apache Software Foundation (http://www.apache.org/)."
*    Alternately, this acknowledgment may appear in the software itself,
*    if and wherever such third-party acknowledgments normally appear.
*
* 4. The names "Apache" and "Apache Software Foundation" must
*    not be used to endorse or promote products derived from this
*    software without prior written permission. For written
*    permission, please contact apache@apache.org.
*
* 5. Products derived from this software may not be called "Apache
*    XMLBeans", nor may "Apache" appear in their name, without prior
*    written permission of the Apache Software Foundation.
*
* THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
* WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
* OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
* ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
* SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
* LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
* USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
* ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
* OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
* OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
* SUCH DAMAGE.
* ====================================================================
*
* This software consists of voluntary contributions made by many
* individuals on behalf of the Apache Software Foundation and was
* originally based on software copyright (c) 2003 BEA Systems
* Inc., <http://www.bea.com/>. For more information on the Apache Software
* Foundation, please see <http://www.apache.org/>.
*/
package org.apache.xmlbeans.impl.binding.bts;

import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.SchemaTypeLoader;
import org.apache.xmlbeans.XmlBeans;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.io.IOException;


/**
 * Represents a BindingLoader whose contents are loaded from a
 * single binding-config file. (See binding-config.xsd)
 */
public class BindingFile extends BaseBindingLoader {

  // ========================================================================
  // Factory

  /**
   * Loader
   */
  public static BindingFile forDoc(org.apache.xml.xmlbeans.bindingConfig.BindingConfigDocument doc) {
    return new BindingFile(doc);
  }

  // ========================================================================
  // Constructors

  /**
   * This constructor is used when making a new one out of the blue.
   */
  public BindingFile() {
    // nothing to do - all maps are empty
  }

  /**
   * This constructor loads an instance from an XML file
   */
  protected BindingFile(org.apache.xml.xmlbeans.bindingConfig.BindingConfigDocument doc) {
    List errors = new ArrayList();
    if (!doc.validate(new XmlOptions().setErrorListener(errors)))
      throw new IllegalArgumentException(errors.size() > 0 ? errors.get(0).toString() : "Invalid binding-config document");

    // todo: in the loops below, validate that entries are unique, or modify schema to do so.

    org.apache.xml.xmlbeans.bindingConfig.BindingType[] btNodes =
            doc.getBindingConfig().getBindings().getBindingTypeArray();
    for (int i = 0; i < btNodes.length; i++) {
      BindingType next = BindingType.loadFromBindingTypeNode(btNodes[i]);
      addBindingType(next, false, false);
    }
    org.apache.xml.xmlbeans.bindingConfig.Mapping[] mNodes =
            doc.getBindingConfig().getJavaToXml().getMappingArray();
    for (int i = 0; i < mNodes.length; i++) {
      JavaTypeName jName = JavaTypeName.forString(mNodes[i].getJavatype());
      XmlTypeName xName = XmlTypeName.forString(mNodes[i].getXmlcomponent());
      addTypeFor(jName, BindingTypeName.forPair(jName, xName));
    }

    mNodes = doc.getBindingConfig().getJavaToElement().getMappingArray();
    for (int i = 0; i < mNodes.length; i++) {
      JavaTypeName jName = JavaTypeName.forString(mNodes[i].getJavatype());
      XmlTypeName xName = XmlTypeName.forString(mNodes[i].getXmlcomponent());
      addElementFor(jName, BindingTypeName.forPair(jName, xName));
    }

    mNodes = doc.getBindingConfig().getXmlToPojo().getMappingArray();
    for (int i = 0; i < mNodes.length; i++) {
      JavaTypeName jName = JavaTypeName.forString(mNodes[i].getJavatype());
      XmlTypeName xName = XmlTypeName.forString(mNodes[i].getXmlcomponent());
      addPojoFor(xName, BindingTypeName.forPair(jName, xName));
    }

    mNodes = doc.getBindingConfig().getXmlToXmlobj().getMappingArray();
    for (int i = 0; i < mNodes.length; i++) {
      JavaTypeName jName = JavaTypeName.forString(mNodes[i].getJavatype());
      XmlTypeName xName = XmlTypeName.forString(mNodes[i].getXmlcomponent());
      addXmlObjectFor(xName, BindingTypeName.forPair(jName, xName));
    }
  }

  // ========================================================================
  // Public methods

  /**
   * Writes out to XML
   */
  public org.apache.xml.xmlbeans.bindingConfig.BindingConfigDocument write()
          throws IOException
  {
    // Here we should use the BindingConfigDocument classloader
    // rather than the thread context classloader.  This is
    // because in some situations (such as when being invoked by ant)
    // the context classloader is potentially weird (because
    // of the design of ant).

    SchemaTypeLoader loader = XmlBeans.typeLoaderForClassLoader(
            org.apache.xml.xmlbeans.bindingConfig.BindingConfigDocument.class.getClassLoader());

    org.apache.xml.xmlbeans.bindingConfig.BindingConfigDocument doc =
            (org.apache.xml.xmlbeans.bindingConfig.BindingConfigDocument)
            loader.newInstance(org.apache.xml.xmlbeans.bindingConfig.BindingConfigDocument.type, null);
    write(doc);
    return doc;
  }


  public void addBindingType(BindingType bType,
                             boolean fromJavaDefault,
                             boolean fromXmlDefault)
  {
    addBindingType(bType);
    if (fromXmlDefault) {
      if (bType.getName().getJavaName().isXmlObject())
        addXmlObjectFor(bType.getName().getXmlName(), bType.getName());
      else
        addPojoFor(bType.getName().getXmlName(), bType.getName());
    }
    if (fromJavaDefault) {
      if (bType.getName().getXmlName().getComponentType() == XmlTypeName.ELEMENT)
        addElementFor(bType.getName().getJavaName(), bType.getName());
      else
        addTypeFor(bType.getName().getJavaName(), bType.getName());
    }
  }

  // ========================================================================
  // Private methods

  /**
   * This function copies an instance into an empty doc.
   */
  private void write(org.apache.xml.xmlbeans.bindingConfig.BindingConfigDocument doc)
  {
    if (doc.getBindingConfig() != null)
      throw new IllegalArgumentException("Can only write into empty doc");
    org.apache.xml.xmlbeans.bindingConfig.BindingConfigDocument.BindingConfig
            bcNode = doc.addNewBindingConfig();

    // make tables
    org.apache.xml.xmlbeans.bindingConfig.BindingTable btabNode = bcNode.addNewBindings();
    org.apache.xml.xmlbeans.bindingConfig.MappingTable typetabNode = bcNode.addNewJavaToXml();
    org.apache.xml.xmlbeans.bindingConfig.MappingTable elementtabNode = bcNode.addNewJavaToElement();
    org.apache.xml.xmlbeans.bindingConfig.MappingTable pojotabNode = bcNode.addNewXmlToPojo();
    org.apache.xml.xmlbeans.bindingConfig.MappingTable xotabNode = bcNode.addNewXmlToXmlobj();

    // fill em in: binding types (delegate to BindingType.write)
    for (Iterator i = bindingTypes().iterator(); i.hasNext();) {
      BindingType bType = (BindingType) i.next();
      org.apache.xml.xmlbeans.bindingConfig.BindingType btNode = btabNode.addNewBindingType();
      bType.write(btNode);
    }
    // from-java mappings
    for (Iterator i = typeMappedJavaTypes().iterator(); i.hasNext();) {
      JavaTypeName jName = (JavaTypeName) i.next();
      BindingTypeName pair = lookupTypeFor(jName);
      org.apache.xml.xmlbeans.bindingConfig.Mapping mNode = typetabNode.addNewMapping();
      mNode.setJavatype(jName.toString());
      mNode.setXmlcomponent(pair.getXmlName().toString());
    }
    // from-java mappings
    for (Iterator i = elementMappedJavaTypes().iterator(); i.hasNext();) {
      JavaTypeName jName = (JavaTypeName) i.next();
      BindingTypeName pair = lookupElementFor(jName);
      org.apache.xml.xmlbeans.bindingConfig.Mapping mNode = elementtabNode.addNewMapping();
      mNode.setJavatype(jName.toString());
      mNode.setXmlcomponent(pair.getXmlName().toString());
    }
    // to-pojo
    for (Iterator i = pojoMappedXmlTypes().iterator(); i.hasNext();) {
      XmlTypeName xName = (XmlTypeName) i.next();
      BindingTypeName pair = lookupPojoFor(xName);
      org.apache.xml.xmlbeans.bindingConfig.Mapping mNode = pojotabNode.addNewMapping();
      mNode.setJavatype(pair.getJavaName().toString());
      mNode.setXmlcomponent(xName.toString());
    }
    // to-xmlobj
    for (Iterator i = xmlObjectMappedXmlTypes().iterator(); i.hasNext();) {
      XmlTypeName xName = (XmlTypeName) i.next();
      BindingTypeName pair = lookupXmlObjectFor(xName);
      org.apache.xml.xmlbeans.bindingConfig.Mapping mNode = xotabNode.addNewMapping();
      mNode.setJavatype(pair.getJavaName().toString());
      mNode.setXmlcomponent(xName.toString());
    }
  }
}