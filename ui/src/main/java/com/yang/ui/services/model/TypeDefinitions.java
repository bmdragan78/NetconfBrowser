package com.yang.ui.services.model;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.opendaylight.yangtools.yang.model.api.IdentitySchemaNode;
import org.opendaylight.yangtools.yang.model.api.TypeDefinition;
import org.opendaylight.yangtools.yang.model.api.type.BinaryTypeDefinition;
import org.opendaylight.yangtools.yang.model.api.type.BitsTypeDefinition;
import org.opendaylight.yangtools.yang.model.api.type.BooleanTypeDefinition;
import org.opendaylight.yangtools.yang.model.api.type.DecimalTypeDefinition;
import org.opendaylight.yangtools.yang.model.api.type.EmptyTypeDefinition;
import org.opendaylight.yangtools.yang.model.api.type.EnumTypeDefinition;
import org.opendaylight.yangtools.yang.model.api.type.IdentityrefTypeDefinition;
import org.opendaylight.yangtools.yang.model.api.type.InstanceIdentifierTypeDefinition;
import org.opendaylight.yangtools.yang.model.api.type.Int16TypeDefinition;
import org.opendaylight.yangtools.yang.model.api.type.Int32TypeDefinition;
import org.opendaylight.yangtools.yang.model.api.type.Int64TypeDefinition;
import org.opendaylight.yangtools.yang.model.api.type.Int8TypeDefinition;
import org.opendaylight.yangtools.yang.model.api.type.LeafrefTypeDefinition;
import org.opendaylight.yangtools.yang.model.api.type.StringTypeDefinition;
import org.opendaylight.yangtools.yang.model.api.type.Uint16TypeDefinition;
import org.opendaylight.yangtools.yang.model.api.type.Uint32TypeDefinition;
import org.opendaylight.yangtools.yang.model.api.type.Uint64TypeDefinition;
import org.opendaylight.yangtools.yang.model.api.type.Uint8TypeDefinition;
import org.opendaylight.yangtools.yang.model.api.type.UnionTypeDefinition;


//Derived from opendaylight Code
final class TypeDefinitions {
	
	
    private TypeDefinitions() {
        throw new UnsupportedOperationException();
    }

    public static String getDefinition(final IdentitySchemaNode identity) {
    	StringBuilder defBuf = new StringBuilder();
    	toStringRec(identity, defBuf);
    	return defBuf.toString();
    }
    
    private static void toStringRec(IdentitySchemaNode identity, StringBuilder defBuf) {
    	defBuf.append(identity.getQName());
    	defBuf.append("\n\n");
    	IdentitySchemaNode baseIdentity = null;
//    	while((baseIdentity = identity.getBaseIdentity()) != null) {
//	    	defBuf.append(baseIdentity.getQName());
//	    	defBuf.append("\n\n");
//	    	identity = baseIdentity;
//	    }
    	Collection<? extends IdentitySchemaNode> baseIdentities = identity.getBaseIdentities();
    	for(IdentitySchemaNode base : baseIdentities) {
    		defBuf.append(base.getQName());
	    	defBuf.append("\n\n");
	    	identity = base;
    	}
	}
    
    
    //return type hierarchy as a type list sorted child->parent
    public static List<TypeDefinition<? extends TypeDefinition<?>>> getTypeList(TypeDefinition type) {
    	List<TypeDefinition<? extends TypeDefinition<?>>> typeList = new ArrayList<TypeDefinition<? extends TypeDefinition<?>>>();
    	typeList.add(type);
    	TypeDefinition baseType = null;
    	while((baseType = type.getBaseType()) != null) {
    		typeList.add(baseType);
	    	type = baseType;
    	}
    	return typeList;
    }

    
    public static String getDefinition(final TypeDefinition type) {
    	StringBuilder defBuf = new StringBuilder();
    	toStringRec(type, defBuf);
    	return defBuf.toString();
    }
    
    private static void toStringRec(TypeDefinition type, StringBuilder defBuf) {
    	defBuf.append(toString(type));
    	defBuf.append("\n\n");
    	TypeDefinition baseType = null;
    	while((baseType = type.getBaseType()) != null) {
    		defBuf.append(toString(baseType));
	    	defBuf.append("\n\n");
	    	type = baseType;
    	}
	}
    
	
    private static String toString(final TypeDefinition type) {
    	
    	if(type instanceof BinaryTypeDefinition) {
    		//return toStringHelper(type).add("length", ((BinaryTypeDefinition)type).getLengthConstraints()).toString();
    		return toStringHelper(type).add("length", ((BinaryTypeDefinition)type).getLengthConstraint()).toString();
    		
    	}else if(type instanceof BitsTypeDefinition) {
    		return toStringHelper(type).add("bits", ((BitsTypeDefinition)type).getBits()).toString();
    		
    	}else if(type instanceof BooleanTypeDefinition) {
    		return toStringHelper(((BooleanTypeDefinition)type)).toString();
    		
    	}else if(type instanceof DecimalTypeDefinition) {
    		//return toStringHelper(type).add("fractionDigits", ((DecimalTypeDefinition)type).getFractionDigits()).add("range", ((DecimalTypeDefinition)type).getRangeConstraints()).toString();
    		return toStringHelper(type).add("fractionDigits", ((DecimalTypeDefinition)type).getFractionDigits()).add("range", ((DecimalTypeDefinition)type).getRangeConstraint()).toString();
    		
    	}else if(type instanceof EmptyTypeDefinition) {
    		return toStringHelper(((EmptyTypeDefinition)type)).toString();
    		
    	}else if(type instanceof EnumTypeDefinition) {
    		return toStringHelper(type).add("values", ((EnumTypeDefinition)type).getValues()).toString();
    		
    	}else if(type instanceof IdentityrefTypeDefinition) {
    		//return toStringHelper(type).add("identity", ((IdentityrefTypeDefinition)type).getIdentity()).toString();
    		return toStringHelper(type).add("identity", ((IdentityrefTypeDefinition)type).toString()).toString();
    		
    	}else if(type instanceof InstanceIdentifierTypeDefinition) {
    		return toStringHelper(type).add("requireInstance", ((InstanceIdentifierTypeDefinition)type).requireInstance()).toString();
    		
    	}else if(type instanceof Int8TypeDefinition) {
    		return toStringHelper(type).add("range", ((Int8TypeDefinition)type).getRangeConstraint()).toString(); 
   		
    	}else if(type instanceof Int16TypeDefinition) {
    		return toStringHelper(type).add("range", ((Int16TypeDefinition)type).getRangeConstraint()).toString(); 
   		
    	}else if(type instanceof Int32TypeDefinition) {
    		return toStringHelper(type).add("range", ((Int32TypeDefinition)type).getRangeConstraint()).toString(); 
    		
    	}else if(type instanceof Int64TypeDefinition) {
    		return toStringHelper(type).add("range", ((Int64TypeDefinition)type).getRangeConstraint()).toString(); 
    		
    	}else if(type instanceof LeafrefTypeDefinition) {
    		 return toStringHelper(type).add("pathStatement", ((LeafrefTypeDefinition)type).getPathStatement()).toString();
    		
    	}else if(type instanceof StringTypeDefinition) {
    		//return toStringHelper(type).add("length", ((StringTypeDefinition)type).getLengthConstraints()).add("patterns", ((StringTypeDefinition)type).getPatternConstraints()).toString();
    		return toStringHelper(type).add("length", ((StringTypeDefinition)type).getLengthConstraint()).add("patterns", ((StringTypeDefinition)type).getPatternConstraints()).toString();
    		
    	}else if(type instanceof UnionTypeDefinition) {
    		return toStringHelper(type).add("types", ((UnionTypeDefinition)type).getTypes()).toString();
    		
    	}else if(type instanceof Uint8TypeDefinition) {
    		return toStringHelper(type).add("range", ((Uint8TypeDefinition)type).getRangeConstraint().get()).toString(); 
   		
    	}else if(type instanceof Uint16TypeDefinition) {
    		return toStringHelper(type).add("range", ((Uint16TypeDefinition)type).getRangeConstraint().get()).toString(); 
   		
    	}else if(type instanceof Uint32TypeDefinition) {
    		return toStringHelper(type).add("range", ((Uint32TypeDefinition)type).getRangeConstraint().get()).toString(); 
    		
    	}else if(type instanceof Uint64TypeDefinition) {
    		return toStringHelper(type).add("range", ((Uint64TypeDefinition)type).getRangeConstraint().get()).toString(); 
    		
    	}
    	return "Type Not Found";
    }
    
    
    private static ToStringHelper toStringHelper(final TypeDefinition<?> type) {
        return MoreObjects.toStringHelper(type).omitNullValues()
               // .add("baseType", type.getBaseType())
                .add("default", type.getDefaultValue())
                //.add("description", type.getDescription())
                //.add("path", type.getPath())
                //.add("reference", type.getReference())
                //.add("status", type.getStatus())
                .add("units", type.getUnits());
    }

}
