/*
* The Apache Software License, Version 1.1
*
*
* Copyright (c) 2000-2003 The Apache Software Foundation.  All rights 
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
* originally based on software copyright (c) 2000-2003 BEA Systems 
* Inc., <http://www.bea.com/>. For more information on the Apache Software
* Foundation, please see <http://www.apache.org/>.
*/

package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.SimpleValue;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.xmlbeans.impl.schema.BuiltinSchemaTypeSystem;

public abstract class JavaIntHolder extends XmlObjectBase
{
    public SchemaType schemaType()
        { return BuiltinSchemaTypeSystem.ST_INT; }

    private int _value;

    // SIMPLE VALUE ACCESSORS BELOW -------------------------------------------

    // gets raw text value
    public String compute_text(NamespaceManager nsm) { return Long.toString(_value); }
    protected void set_text(String s)
    {
        try { set_int(Integer.parseInt(s)); }
        catch (Exception e) { throw new XmlValueOutOfRangeException(); }
    }
    protected void set_nil()
    {
        _value = 0;
    }
    // numerics: fractional
    public BigDecimal bigDecimalValue() { check_dated(); return new BigDecimal(_value); }
    public BigInteger bigIntegerValue() { check_dated(); return BigInteger.valueOf(_value); }
    public long longValue() { check_dated(); return _value; }
    public int intValue() { check_dated(); return _value; }

    static final BigInteger _max = BigInteger.valueOf(Integer.MAX_VALUE);
    static final BigInteger _min = BigInteger.valueOf(Integer.MIN_VALUE);

    // setters
    protected void set_BigDecimal(BigDecimal v) { set_BigInteger(v.toBigInteger()); }
    protected void set_BigInteger(BigInteger v)
    {
        if (v.compareTo(_max) > 0 || v.compareTo(_min) < 0)
            throw new XmlValueOutOfRangeException();
        set_int(v.intValue());
    }
    protected void set_long(long l)
    {
        if (l > Integer.MAX_VALUE || l < Integer.MIN_VALUE)
            throw new XmlValueOutOfRangeException();
        set_int((int)l);
    }
    protected void set_int(int i)
    {
        _value = i;
    }

    // comparators
    protected int compare_to(XmlObject i)
    {
        if (((SimpleValue)i).instanceType().getDecimalSize() > SchemaType.SIZE_INT)
            return -i.compareTo(this);

        return _value == ((XmlObjectBase)i).intValue() ? 0 :
               _value < ((XmlObjectBase)i).intValue() ? -1 : 1;
    }

    protected boolean equal_to(XmlObject i)
    {
        if (((SimpleValue)i).instanceType().getDecimalSize() > SchemaType.SIZE_INT)
            return i.valueEquals(this);

        return _value == ((XmlObjectBase)i).intValue();
    }

    /**
     * Note, this is carefully aligned with hash codes for all xsd:decimal
     * primitives.
     */
    protected int value_hash_code()
    {
        return _value;
    }
}
