/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer;

import io.github.douira.glsl_transformer.GLSLParserListener;
import io.github.douira.glsl_transformer.GLSLParserVisitor;
import io.github.douira.glsl_transformer.parser.ExtendedParser;
import java.util.ArrayList;
import java.util.List;
import oculus.org.antlr.v4.runtime.FailedPredicateException;
import oculus.org.antlr.v4.runtime.NoViableAltException;
import oculus.org.antlr.v4.runtime.ParserRuleContext;
import oculus.org.antlr.v4.runtime.RecognitionException;
import oculus.org.antlr.v4.runtime.RuleContext;
import oculus.org.antlr.v4.runtime.RuntimeMetaData;
import oculus.org.antlr.v4.runtime.Token;
import oculus.org.antlr.v4.runtime.TokenStream;
import oculus.org.antlr.v4.runtime.Vocabulary;
import oculus.org.antlr.v4.runtime.VocabularyImpl;
import oculus.org.antlr.v4.runtime.atn.ATN;
import oculus.org.antlr.v4.runtime.atn.ATNDeserializer;
import oculus.org.antlr.v4.runtime.atn.ParserATNSimulator;
import oculus.org.antlr.v4.runtime.atn.PredictionContextCache;
import oculus.org.antlr.v4.runtime.dfa.DFA;
import oculus.org.antlr.v4.runtime.tree.ParseTreeListener;
import oculus.org.antlr.v4.runtime.tree.ParseTreeVisitor;
import oculus.org.antlr.v4.runtime.tree.TerminalNode;

public class GLSLParser
extends ExtendedParser {
    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache;
    public static final int COLON = 1;
    public static final int UNIFORM = 2;
    public static final int BUFFER = 3;
    public static final int IN = 4;
    public static final int OUT = 5;
    public static final int INOUT = 6;
    public static final int HIGHP = 7;
    public static final int MEDIUMP = 8;
    public static final int LOWP = 9;
    public static final int PRECISION = 10;
    public static final int CONST = 11;
    public static final int PRECISE = 12;
    public static final int INVARIANT = 13;
    public static final int SMOOTH = 14;
    public static final int FLAT = 15;
    public static final int CENTROID = 16;
    public static final int ATTRIBUTE = 17;
    public static final int VOLATILE = 18;
    public static final int VARYING = 19;
    public static final int SHARED = 20;
    public static final int LAYOUT = 21;
    public static final int DOT_LENGTH_METHOD_CALL = 22;
    public static final int NOPERSPECTIVE = 23;
    public static final int SAMPLE = 24;
    public static final int PATCH = 25;
    public static final int COHERENT = 26;
    public static final int RESTRICT = 27;
    public static final int READONLY = 28;
    public static final int WRITEONLY = 29;
    public static final int SUBROUTINE = 30;
    public static final int DEVICECOHERENT = 31;
    public static final int QUEUEFAMILYCOHERENT = 32;
    public static final int WORKGROUPCOHERENT = 33;
    public static final int SUBGROUPCOHERENT = 34;
    public static final int NONPRIVATE = 35;
    public static final int RAY_PAYLOAD_EXT = 36;
    public static final int RAY_PAYLOAD_IN_EXT = 37;
    public static final int HIT_ATTRIBUTE_EXT = 38;
    public static final int CALLABLE_DATA_EXT = 39;
    public static final int CALLABLE_DATA_IN_EXT = 40;
    public static final int IGNORE_INTERSECTION_EXT = 41;
    public static final int TERMINATE_RAY_EXT = 42;
    public static final int ACCELERATION_STRUCTURE_EXT = 43;
    public static final int ATOMIC_UINT = 44;
    public static final int STRUCT = 45;
    public static final int IF = 46;
    public static final int ELSE = 47;
    public static final int SWITCH = 48;
    public static final int CASE = 49;
    public static final int DEFAULT = 50;
    public static final int WHILE = 51;
    public static final int DO = 52;
    public static final int FOR = 53;
    public static final int CONTINUE = 54;
    public static final int BREAK = 55;
    public static final int RETURN = 56;
    public static final int DISCARD = 57;
    public static final int DEMOTE = 58;
    public static final int UINT16CONSTANT = 59;
    public static final int INT16CONSTANT = 60;
    public static final int UINT32CONSTANT = 61;
    public static final int INT32CONSTANT = 62;
    public static final int UINT64CONSTANT = 63;
    public static final int INT64CONSTANT = 64;
    public static final int FLOAT16CONSTANT = 65;
    public static final int FLOAT32CONSTANT = 66;
    public static final int FLOAT64CONSTANT = 67;
    public static final int BOOLCONSTANT = 68;
    public static final int BOOL = 69;
    public static final int BVEC2 = 70;
    public static final int BVEC3 = 71;
    public static final int BVEC4 = 72;
    public static final int INT8 = 73;
    public static final int I8VEC2 = 74;
    public static final int I8VEC3 = 75;
    public static final int I8VEC4 = 76;
    public static final int UINT8 = 77;
    public static final int U8VEC2 = 78;
    public static final int U8VEC3 = 79;
    public static final int U8VEC4 = 80;
    public static final int INT16 = 81;
    public static final int I16VEC2 = 82;
    public static final int I16VEC3 = 83;
    public static final int I16VEC4 = 84;
    public static final int UINT16 = 85;
    public static final int U16VEC2 = 86;
    public static final int U16VEC3 = 87;
    public static final int U16VEC4 = 88;
    public static final int INT32 = 89;
    public static final int I32VEC2 = 90;
    public static final int I32VEC3 = 91;
    public static final int I32VEC4 = 92;
    public static final int UINT32 = 93;
    public static final int U32VEC2 = 94;
    public static final int U32VEC3 = 95;
    public static final int U32VEC4 = 96;
    public static final int INT64 = 97;
    public static final int I64VEC2 = 98;
    public static final int I64VEC3 = 99;
    public static final int I64VEC4 = 100;
    public static final int UINT64 = 101;
    public static final int U64VEC2 = 102;
    public static final int U64VEC3 = 103;
    public static final int U64VEC4 = 104;
    public static final int FLOAT16 = 105;
    public static final int F16VEC2 = 106;
    public static final int F16VEC3 = 107;
    public static final int F16VEC4 = 108;
    public static final int F16MAT2X2 = 109;
    public static final int F16MAT2X3 = 110;
    public static final int F16MAT2X4 = 111;
    public static final int F16MAT3X2 = 112;
    public static final int F16MAT3X3 = 113;
    public static final int F16MAT3X4 = 114;
    public static final int F16MAT4X2 = 115;
    public static final int F16MAT4X3 = 116;
    public static final int F16MAT4X4 = 117;
    public static final int FLOAT32 = 118;
    public static final int F32VEC2 = 119;
    public static final int F32VEC3 = 120;
    public static final int F32VEC4 = 121;
    public static final int F32MAT2X2 = 122;
    public static final int F32MAT2X3 = 123;
    public static final int F32MAT2X4 = 124;
    public static final int F32MAT3X2 = 125;
    public static final int F32MAT3X3 = 126;
    public static final int F32MAT3X4 = 127;
    public static final int F32MAT4X2 = 128;
    public static final int F32MAT4X3 = 129;
    public static final int F32MAT4X4 = 130;
    public static final int FLOAT64 = 131;
    public static final int F64VEC2 = 132;
    public static final int F64VEC3 = 133;
    public static final int F64VEC4 = 134;
    public static final int F64MAT2X2 = 135;
    public static final int F64MAT2X3 = 136;
    public static final int F64MAT2X4 = 137;
    public static final int F64MAT3X2 = 138;
    public static final int F64MAT3X3 = 139;
    public static final int F64MAT3X4 = 140;
    public static final int F64MAT4X2 = 141;
    public static final int F64MAT4X3 = 142;
    public static final int F64MAT4X4 = 143;
    public static final int IMAGE1D = 144;
    public static final int IMAGE2D = 145;
    public static final int IMAGE3D = 146;
    public static final int UIMAGE1D = 147;
    public static final int UIMAGE2D = 148;
    public static final int UIMAGE3D = 149;
    public static final int IIMAGE1D = 150;
    public static final int IIMAGE2D = 151;
    public static final int IIMAGE3D = 152;
    public static final int SAMPLER1D = 153;
    public static final int SAMPLER2D = 154;
    public static final int SAMPLER3D = 155;
    public static final int SAMPLER2DRECT = 156;
    public static final int SAMPLER1DSHADOW = 157;
    public static final int SAMPLER2DSHADOW = 158;
    public static final int SAMPLER2DRECTSHADOW = 159;
    public static final int SAMPLER1DARRAY = 160;
    public static final int SAMPLER2DARRAY = 161;
    public static final int SAMPLER1DARRAYSHADOW = 162;
    public static final int SAMPLER2DARRAYSHADOW = 163;
    public static final int ISAMPLER1D = 164;
    public static final int ISAMPLER2D = 165;
    public static final int ISAMPLER2DRECT = 166;
    public static final int ISAMPLER3D = 167;
    public static final int ISAMPLER1DARRAY = 168;
    public static final int ISAMPLER2DARRAY = 169;
    public static final int USAMPLER1D = 170;
    public static final int USAMPLER2D = 171;
    public static final int USAMPLER2DRECT = 172;
    public static final int USAMPLER3D = 173;
    public static final int USAMPLER1DARRAY = 174;
    public static final int USAMPLER2DARRAY = 175;
    public static final int SAMPLER2DMS = 176;
    public static final int ISAMPLER2DMS = 177;
    public static final int USAMPLER2DMS = 178;
    public static final int SAMPLER2DMSARRAY = 179;
    public static final int ISAMPLER2DMSARRAY = 180;
    public static final int USAMPLER2DMSARRAY = 181;
    public static final int IMAGE2DRECT = 182;
    public static final int IMAGE1DARRAY = 183;
    public static final int IMAGE2DARRAY = 184;
    public static final int IMAGE2DMS = 185;
    public static final int IMAGE2DMSARRAY = 186;
    public static final int IIMAGE2DRECT = 187;
    public static final int IIMAGE1DARRAY = 188;
    public static final int IIMAGE2DARRAY = 189;
    public static final int IIMAGE2DMS = 190;
    public static final int IIMAGE2DMSARRAY = 191;
    public static final int UIMAGE2DRECT = 192;
    public static final int UIMAGE1DARRAY = 193;
    public static final int UIMAGE2DARRAY = 194;
    public static final int UIMAGE2DMS = 195;
    public static final int UIMAGE2DMSARRAY = 196;
    public static final int SAMPLERCUBESHADOW = 197;
    public static final int SAMPLERCUBEARRAYSHADOW = 198;
    public static final int SAMPLERCUBE = 199;
    public static final int ISAMPLERCUBE = 200;
    public static final int USAMPLERCUBE = 201;
    public static final int SAMPLERBUFFER = 202;
    public static final int ISAMPLERBUFFER = 203;
    public static final int USAMPLERBUFFER = 204;
    public static final int SAMPLERCUBEARRAY = 205;
    public static final int ISAMPLERCUBEARRAY = 206;
    public static final int USAMPLERCUBEARRAY = 207;
    public static final int IMAGECUBE = 208;
    public static final int UIMAGECUBE = 209;
    public static final int IIMAGECUBE = 210;
    public static final int IMAGEBUFFER = 211;
    public static final int IIMAGEBUFFER = 212;
    public static final int UIMAGEBUFFER = 213;
    public static final int IMAGECUBEARRAY = 214;
    public static final int IIMAGECUBEARRAY = 215;
    public static final int UIMAGECUBEARRAY = 216;
    public static final int INC_OP = 217;
    public static final int DEC_OP = 218;
    public static final int VOID = 219;
    public static final int LEFT_OP = 220;
    public static final int RIGHT_OP = 221;
    public static final int LE_OP = 222;
    public static final int GE_OP = 223;
    public static final int EQ_OP = 224;
    public static final int NE_OP = 225;
    public static final int LOGICAL_AND_OP = 226;
    public static final int LOGICAL_XOR_OP = 227;
    public static final int LOGICAL_OR_OP = 228;
    public static final int MUL_ASSIGN = 229;
    public static final int DIV_ASSIGN = 230;
    public static final int MOD_ASSIGN = 231;
    public static final int ADD_ASSIGN = 232;
    public static final int SUB_ASSIGN = 233;
    public static final int LEFT_ASSIGN = 234;
    public static final int RIGHT_ASSIGN = 235;
    public static final int AND_ASSIGN = 236;
    public static final int XOR_ASSIGN = 237;
    public static final int OR_ASSIGN = 238;
    public static final int LPAREN = 239;
    public static final int RPAREN = 240;
    public static final int LBRACE = 241;
    public static final int RBRACE = 242;
    public static final int SEMICOLON = 243;
    public static final int LBRACKET = 244;
    public static final int RBRACKET = 245;
    public static final int COMMA = 246;
    public static final int DOT = 247;
    public static final int PLUS_OP = 248;
    public static final int MINUS_OP = 249;
    public static final int LOGICAL_NOT_OP = 250;
    public static final int BITWISE_NEG_OP = 251;
    public static final int TIMES_OP = 252;
    public static final int DIV_OP = 253;
    public static final int MOD_OP = 254;
    public static final int LT_OP = 255;
    public static final int GT_OP = 256;
    public static final int BITWISE_AND_OP = 257;
    public static final int BITWISE_OR_OP = 258;
    public static final int BITWISE_XOR_OP = 259;
    public static final int QUERY_OP = 260;
    public static final int ASSIGN_OP = 261;
    public static final int PP_ENTER_MODE = 262;
    public static final int PP_EMPTY = 263;
    public static final int NR_LINE = 264;
    public static final int NR = 265;
    public static final int IDENTIFIER = 266;
    public static final int LINE_CONTINUE = 267;
    public static final int LINE_COMMENT = 268;
    public static final int BLOCK_COMMENT = 269;
    public static final int WS = 270;
    public static final int EOL = 271;
    public static final int NR_EXTENSION = 272;
    public static final int NR_VERSION = 273;
    public static final int NR_CUSTOM = 274;
    public static final int NR_INCLUDE = 275;
    public static final int NR_PRAGMA = 276;
    public static final int NR_PRAGMA_DEBUG = 277;
    public static final int NR_PRAGMA_OPTIMIZE = 278;
    public static final int NR_PRAGMA_INVARIANT = 279;
    public static final int NR_ON = 280;
    public static final int NR_OFF = 281;
    public static final int NR_ALL = 282;
    public static final int NR_REQUIRE = 283;
    public static final int NR_ENABLE = 284;
    public static final int NR_WARN = 285;
    public static final int NR_DISABLE = 286;
    public static final int NR_COLON = 287;
    public static final int NR_LPAREN = 288;
    public static final int NR_RPAREN = 289;
    public static final int NR_STDGL = 290;
    public static final int NR_CORE = 291;
    public static final int NR_COMPATIBILITY = 292;
    public static final int NR_ES = 293;
    public static final int NR_GLSL_110 = 294;
    public static final int NR_GLSL_120 = 295;
    public static final int NR_GLSLES_100 = 296;
    public static final int NR_GLSL_130 = 297;
    public static final int NR_GLSL_140 = 298;
    public static final int NR_GLSL_150 = 299;
    public static final int NR_GLSL_330 = 300;
    public static final int NR_GLSLES_300 = 301;
    public static final int NR_GLSLES_310 = 302;
    public static final int NR_GLSLES_320 = 303;
    public static final int NR_GLSL_400 = 304;
    public static final int NR_GLSL_410 = 305;
    public static final int NR_GLSL_420 = 306;
    public static final int NR_GLSL_430 = 307;
    public static final int NR_GLSL_440 = 308;
    public static final int NR_GLSL_450 = 309;
    public static final int NR_GLSL_460 = 310;
    public static final int NR_STRING_START = 311;
    public static final int NR_STRING_START_ANGLE = 312;
    public static final int NR_INTCONSTANT = 313;
    public static final int NR_IDENTIFIER = 314;
    public static final int NR_LINE_CONTINUE = 315;
    public static final int NR_LINE_COMMENT = 316;
    public static final int NR_BLOCK_COMMENT = 317;
    public static final int NR_EOL = 318;
    public static final int NR_WS = 319;
    public static final int S_CONTENT = 320;
    public static final int S_STRING_END = 321;
    public static final int S_CONTENT_ANGLE = 322;
    public static final int S_STRING_END_ANGLE = 323;
    public static final int C_LINE_COMMENT = 324;
    public static final int C_BLOCK_COMMENT = 325;
    public static final int C_EOL = 326;
    public static final int C_WS = 327;
    public static final int C_CONTENT = 328;
    public static final int PP_LINE_CONTINUE = 329;
    public static final int PP_LINE_COMMENT = 330;
    public static final int PP_BLOCK_COMMENT = 331;
    public static final int PP_EOL = 332;
    public static final int PP_CONTENT = 333;
    public static final int RULE_translationUnit = 0;
    public static final int RULE_versionStatement = 1;
    public static final int RULE_externalDeclaration = 2;
    public static final int RULE_emptyDeclaration = 3;
    public static final int RULE_pragmaDirective = 4;
    public static final int RULE_extensionDirective = 5;
    public static final int RULE_customDirective = 6;
    public static final int RULE_includeDirective = 7;
    public static final int RULE_layoutDefaults = 8;
    public static final int RULE_functionDefinition = 9;
    public static final int RULE_finiteExpression = 10;
    public static final int RULE_expression = 11;
    public static final int RULE_declaration = 12;
    public static final int RULE_functionPrototype = 13;
    public static final int RULE_functionParameterList = 14;
    public static final int RULE_parameterDeclaration = 15;
    public static final int RULE_attribute = 16;
    public static final int RULE_singleAttribute = 17;
    public static final int RULE_declarationMember = 18;
    public static final int RULE_fullySpecifiedType = 19;
    public static final int RULE_storageQualifier = 20;
    public static final int RULE_layoutQualifier = 21;
    public static final int RULE_layoutQualifierId = 22;
    public static final int RULE_precisionQualifier = 23;
    public static final int RULE_interpolationQualifier = 24;
    public static final int RULE_invariantQualifier = 25;
    public static final int RULE_preciseQualifier = 26;
    public static final int RULE_typeQualifier = 27;
    public static final int RULE_typeSpecifier = 28;
    public static final int RULE_arraySpecifier = 29;
    public static final int RULE_arraySpecifierSegment = 30;
    public static final int RULE_builtinTypeSpecifierParseable = 31;
    public static final int RULE_builtinTypeSpecifierFixed = 32;
    public static final int RULE_structSpecifier = 33;
    public static final int RULE_structBody = 34;
    public static final int RULE_structMember = 35;
    public static final int RULE_structDeclarator = 36;
    public static final int RULE_initializer = 37;
    public static final int RULE_statement = 38;
    public static final int RULE_compoundStatement = 39;
    public static final int RULE_declarationStatement = 40;
    public static final int RULE_expressionStatement = 41;
    public static final int RULE_emptyStatement = 42;
    public static final int RULE_selectionStatement = 43;
    public static final int RULE_iterationCondition = 44;
    public static final int RULE_switchStatement = 45;
    public static final int RULE_caseLabel = 46;
    public static final int RULE_whileStatement = 47;
    public static final int RULE_doWhileStatement = 48;
    public static final int RULE_forStatement = 49;
    public static final int RULE_jumpStatement = 50;
    public static final int RULE_demoteStatement = 51;
    public static final String[] ruleNames;
    private static final String[] _LITERAL_NAMES;
    private static final String[] _SYMBOLIC_NAMES;
    public static final Vocabulary VOCABULARY;
    @Deprecated
    public static final String[] tokenNames;
    public static final String _serializedATN = "\u0004\u0001\u014d\u02a9\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007\u001b\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d\u0002\u001e\u0007\u001e\u0002\u001f\u0007\u001f\u0002 \u0007 \u0002!\u0007!\u0002\"\u0007\"\u0002#\u0007#\u0002$\u0007$\u0002%\u0007%\u0002&\u0007&\u0002'\u0007'\u0002(\u0007(\u0002)\u0007)\u0002*\u0007*\u0002+\u0007+\u0002,\u0007,\u0002-\u0007-\u0002.\u0007.\u0002/\u0007/\u00020\u00070\u00021\u00071\u00022\u00072\u00023\u00073\u0001\u0000\u0003\u0000j\b\u0000\u0001\u0000\u0005\u0000m\b\u0000\n\u0000\f\u0000p\t\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0003\u0001x\b\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0003\u0002\u0084\b\u0002\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0003\u0004\u008b\b\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0003\u0004\u0096\b\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0003\u0005\u009f\b\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0003\u0006\u00a6\b\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0003\u0007\u00ae\b\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0003\u0007\u00b3\b\u0007\u0001\u0007\u0003\u0007\u00b6\b\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0003\n\u00ca\b\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0005\n\u00d2\b\n\n\n\f\n\u00d5\t\n\u0003\n\u00d7\b\n\u0001\n\u0001\n\u0001\n\u0003\n\u00dc\b\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0005\n\u0114\b\n\n\n\f\n\u0117\t\n\u0001\u000b\u0001\u000b\u0001\u000b\u0005\u000b\u011c\b\u000b\n\u000b\f\u000b\u011f\t\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0003\f\u0129\b\f\u0003\f\u012b\b\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0005\f\u0133\b\f\n\f\f\f\u0136\t\f\u0003\f\u0138\b\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0005\f\u0145\b\f\n\f\f\f\u0148\t\f\u0003\f\u014a\b\f\u0001\f\u0001\f\u0003\f\u014e\b\f\u0001\r\u0003\r\u0151\b\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0003\r\u0159\b\r\u0001\u000e\u0001\u000e\u0001\u000e\u0005\u000e\u015e\b\u000e\n\u000e\f\u000e\u0161\t\u000e\u0003\u000e\u0163\b\u000e\u0001\u000f\u0001\u000f\u0001\u000f\u0003\u000f\u0168\b\u000f\u0003\u000f\u016a\b\u000f\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0005\u0010\u0171\b\u0010\n\u0010\f\u0010\u0174\t\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0011\u0001\u0011\u0001\u0011\u0003\u0011\u017c\b\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0003\u0011\u0183\b\u0011\u0001\u0012\u0001\u0012\u0003\u0012\u0187\b\u0012\u0001\u0012\u0001\u0012\u0003\u0012\u018b\b\u0012\u0001\u0013\u0003\u0013\u018e\b\u0013\u0001\u0013\u0001\u0013\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0005\u0014\u01a8\b\u0014\n\u0014\f\u0014\u01ab\t\u0014\u0001\u0014\u0003\u0014\u01ae\b\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0003\u0014\u01ba\b\u0014\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0005\u0015\u01c1\b\u0015\n\u0015\f\u0015\u01c4\t\u0015\u0001\u0015\u0001\u0015\u0001\u0016\u0001\u0016\u0001\u0016\u0003\u0016\u01cb\b\u0016\u0001\u0016\u0003\u0016\u01ce\b\u0016\u0001\u0017\u0001\u0017\u0001\u0018\u0001\u0018\u0001\u0019\u0001\u0019\u0001\u001a\u0001\u001a\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0004\u001b\u01de\b\u001b\u000b\u001b\f\u001b\u01df\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0003\u001c\u01e6\b\u001c\u0001\u001c\u0003\u001c\u01e9\b\u001c\u0001\u001d\u0004\u001d\u01ec\b\u001d\u000b\u001d\f\u001d\u01ed\u0001\u001e\u0001\u001e\u0003\u001e\u01f2\b\u001e\u0001\u001e\u0001\u001e\u0001\u001f\u0001\u001f\u0001 \u0001 \u0001!\u0001!\u0003!\u01fc\b!\u0001!\u0001!\u0001\"\u0001\"\u0004\"\u0202\b\"\u000b\"\f\"\u0203\u0001\"\u0001\"\u0001#\u0001#\u0001#\u0001#\u0005#\u020c\b#\n#\f#\u020f\t#\u0001#\u0001#\u0001$\u0001$\u0003$\u0215\b$\u0001%\u0001%\u0001%\u0001%\u0001%\u0005%\u021c\b%\n%\f%\u021f\t%\u0001%\u0003%\u0222\b%\u0003%\u0224\b%\u0001%\u0003%\u0227\b%\u0001&\u0001&\u0001&\u0001&\u0001&\u0001&\u0001&\u0001&\u0001&\u0001&\u0001&\u0001&\u0003&\u0235\b&\u0001'\u0001'\u0005'\u0239\b'\n'\f'\u023c\t'\u0001'\u0001'\u0001(\u0001(\u0001)\u0001)\u0001)\u0001*\u0001*\u0001+\u0003+\u0248\b+\u0001+\u0001+\u0001+\u0001+\u0001+\u0001+\u0001+\u0003+\u0251\b+\u0001,\u0001,\u0001,\u0001,\u0001,\u0001-\u0003-\u0259\b-\u0001-\u0001-\u0001-\u0001-\u0001-\u0001-\u0001.\u0001.\u0001.\u0001.\u0001.\u0001.\u0003.\u0267\b.\u0001/\u0003/\u026a\b/\u0001/\u0001/\u0001/\u0001/\u0003/\u0270\b/\u0001/\u0001/\u0001/\u00010\u00030\u0276\b0\u00010\u00010\u00010\u00010\u00010\u00010\u00010\u00010\u00011\u00031\u0281\b1\u00011\u00011\u00011\u00011\u00011\u00031\u0288\b1\u00011\u00011\u00031\u028c\b1\u00011\u00011\u00031\u0290\b1\u00011\u00011\u00011\u00012\u00012\u00012\u00012\u00012\u00012\u00032\u029b\b2\u00012\u00012\u00012\u00012\u00012\u00012\u00012\u00032\u02a4\b2\u00013\u00013\u00013\u00013\u0000\u0001\u00144\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u001e \"$&(*,.02468:<>@BDFHJLNPRTVXZ\\^`bdf\u0000\u0013\u0001\u0000\u0126\u0136\u0001\u0000\u0123\u0125\u0001\u0000\u0115\u0116\u0001\u0000\u0118\u0119\u0001\u0000\u011b\u011e\u0001\u0000\u0002\u0005\u0001\u0000;D\u0002\u0000\u00d9\u00da\u00f8\u00fb\u0001\u0000\u00fc\u00fe\u0001\u0000\u00f8\u00f9\u0001\u0000\u00dc\u00dd\u0002\u0000\u00de\u00df\u00ff\u0100\u0001\u0000\u00e0\u00e1\u0002\u0000\u00e5\u00ee\u0105\u0105\u0001\u0000\u00d9\u00da\u0001\u0000\u0007\t\u0002\u0000\u000e\u000f\u0017\u0017\u0001\u0000E\u008f\u0003\u0000+,\u0090\u00d8\u00db\u00db\u0309\u0000i\u0001\u0000\u0000\u0000\u0002s\u0001\u0000\u0000\u0000\u0004\u0083\u0001\u0000\u0000\u0000\u0006\u0085\u0001\u0000\u0000\u0000\b\u0087\u0001\u0000\u0000\u0000\n\u0099\u0001\u0000\u0000\u0000\f\u00a2\u0001\u0000\u0000\u0000\u000e\u00a9\u0001\u0000\u0000\u0000\u0010\u00b9\u0001\u0000\u0000\u0000\u0012\u00bd\u0001\u0000\u0000\u0000\u0014\u00db\u0001\u0000\u0000\u0000\u0016\u0118\u0001\u0000\u0000\u0000\u0018\u014d\u0001\u0000\u0000\u0000\u001a\u0150\u0001\u0000\u0000\u0000\u001c\u0162\u0001\u0000\u0000\u0000\u001e\u0164\u0001\u0000\u0000\u0000 \u016b\u0001\u0000\u0000\u0000\"\u017b\u0001\u0000\u0000\u0000$\u0184\u0001\u0000\u0000\u0000&\u018d\u0001\u0000\u0000\u0000(\u01b9\u0001\u0000\u0000\u0000*\u01bb\u0001\u0000\u0000\u0000,\u01cd\u0001\u0000\u0000\u0000.\u01cf\u0001\u0000\u0000\u00000\u01d1\u0001\u0000\u0000\u00002\u01d3\u0001\u0000\u0000\u00004\u01d5\u0001\u0000\u0000\u00006\u01dd\u0001\u0000\u0000\u00008\u01e5\u0001\u0000\u0000\u0000:\u01eb\u0001\u0000\u0000\u0000<\u01ef\u0001\u0000\u0000\u0000>\u01f5\u0001\u0000\u0000\u0000@\u01f7\u0001\u0000\u0000\u0000B\u01f9\u0001\u0000\u0000\u0000D\u01ff\u0001\u0000\u0000\u0000F\u0207\u0001\u0000\u0000\u0000H\u0212\u0001\u0000\u0000\u0000J\u0226\u0001\u0000\u0000\u0000L\u0234\u0001\u0000\u0000\u0000N\u0236\u0001\u0000\u0000\u0000P\u023f\u0001\u0000\u0000\u0000R\u0241\u0001\u0000\u0000\u0000T\u0244\u0001\u0000\u0000\u0000V\u0247\u0001\u0000\u0000\u0000X\u0252\u0001\u0000\u0000\u0000Z\u0258\u0001\u0000\u0000\u0000\\\u0266\u0001\u0000\u0000\u0000^\u0269\u0001\u0000\u0000\u0000`\u0275\u0001\u0000\u0000\u0000b\u0280\u0001\u0000\u0000\u0000d\u02a3\u0001\u0000\u0000\u0000f\u02a5\u0001\u0000\u0000\u0000hj\u0003\u0002\u0001\u0000ih\u0001\u0000\u0000\u0000ij\u0001\u0000\u0000\u0000jn\u0001\u0000\u0000\u0000km\u0003\u0004\u0002\u0000lk\u0001\u0000\u0000\u0000mp\u0001\u0000\u0000\u0000nl\u0001\u0000\u0000\u0000no\u0001\u0000\u0000\u0000oq\u0001\u0000\u0000\u0000pn\u0001\u0000\u0000\u0000qr\u0005\u0000\u0000\u0001r\u0001\u0001\u0000\u0000\u0000st\u0005\u0109\u0000\u0000tu\u0005\u0111\u0000\u0000uw\u0007\u0000\u0000\u0000vx\u0007\u0001\u0000\u0000wv\u0001\u0000\u0000\u0000wx\u0001\u0000\u0000\u0000xy\u0001\u0000\u0000\u0000yz\u0005\u013e\u0000\u0000z\u0003\u0001\u0000\u0000\u0000{\u0084\u0003\u0012\t\u0000|\u0084\u0003\u0018\f\u0000}\u0084\u0003\b\u0004\u0000~\u0084\u0003\n\u0005\u0000\u007f\u0084\u0003\f\u0006\u0000\u0080\u0084\u0003\u000e\u0007\u0000\u0081\u0084\u0003\u0010\b\u0000\u0082\u0084\u0003\u0006\u0003\u0000\u0083{\u0001\u0000\u0000\u0000\u0083|\u0001\u0000\u0000\u0000\u0083}\u0001\u0000\u0000\u0000\u0083~\u0001\u0000\u0000\u0000\u0083\u007f\u0001\u0000\u0000\u0000\u0083\u0080\u0001\u0000\u0000\u0000\u0083\u0081\u0001\u0000\u0000\u0000\u0083\u0082\u0001\u0000\u0000\u0000\u0084\u0005\u0001\u0000\u0000\u0000\u0085\u0086\u0005\u00f3\u0000\u0000\u0086\u0007\u0001\u0000\u0000\u0000\u0087\u0088\u0005\u0109\u0000\u0000\u0088\u008a\u0005\u0114\u0000\u0000\u0089\u008b\u0005\u0122\u0000\u0000\u008a\u0089\u0001\u0000\u0000\u0000\u008a\u008b\u0001\u0000\u0000\u0000\u008b\u0095\u0001\u0000\u0000\u0000\u008c\u0096\u0005\u013a\u0000\u0000\u008d\u008e\u0007\u0002\u0000\u0000\u008e\u008f\u0005\u0120\u0000\u0000\u008f\u0090\u0007\u0003\u0000\u0000\u0090\u0096\u0005\u0121\u0000\u0000\u0091\u0092\u0005\u0117\u0000\u0000\u0092\u0093\u0005\u0120\u0000\u0000\u0093\u0094\u0005\u011a\u0000\u0000\u0094\u0096\u0005\u0121\u0000\u0000\u0095\u008c\u0001\u0000\u0000\u0000\u0095\u008d\u0001\u0000\u0000\u0000\u0095\u0091\u0001\u0000\u0000\u0000\u0096\u0097\u0001\u0000\u0000\u0000\u0097\u0098\u0005\u013e\u0000\u0000\u0098\t\u0001\u0000\u0000\u0000\u0099\u009a\u0005\u0109\u0000\u0000\u009a\u009b\u0005\u0110\u0000\u0000\u009b\u009e\u0005\u013a\u0000\u0000\u009c\u009d\u0005\u011f\u0000\u0000\u009d\u009f\u0007\u0004\u0000\u0000\u009e\u009c\u0001\u0000\u0000\u0000\u009e\u009f\u0001\u0000\u0000\u0000\u009f\u00a0\u0001\u0000\u0000\u0000\u00a0\u00a1\u0005\u013e\u0000\u0000\u00a1\u000b\u0001\u0000\u0000\u0000\u00a2\u00a3\u0005\u0109\u0000\u0000\u00a3\u00a5\u0005\u0112\u0000\u0000\u00a4\u00a6\u0005\u0148\u0000\u0000\u00a5\u00a4\u0001\u0000\u0000\u0000\u00a5\u00a6\u0001\u0000\u0000\u0000\u00a6\u00a7\u0001\u0000\u0000\u0000\u00a7\u00a8\u0005\u0146\u0000\u0000\u00a8\r\u0001\u0000\u0000\u0000\u00a9\u00aa\u0005\u0109\u0000\u0000\u00aa\u00b5\u0005\u0113\u0000\u0000\u00ab\u00ad\u0005\u0137\u0000\u0000\u00ac\u00ae\u0005\u0140\u0000\u0000\u00ad\u00ac\u0001\u0000\u0000\u0000\u00ad\u00ae\u0001\u0000\u0000\u0000\u00ae\u00af\u0001\u0000\u0000\u0000\u00af\u00b6\u0005\u0141\u0000\u0000\u00b0\u00b2\u0005\u0138\u0000\u0000\u00b1\u00b3\u0005\u0142\u0000\u0000\u00b2\u00b1\u0001\u0000\u0000\u0000\u00b2\u00b3\u0001\u0000\u0000\u0000\u00b3\u00b4\u0001\u0000\u0000\u0000\u00b4\u00b6\u0005\u0143\u0000\u0000\u00b5\u00ab\u0001\u0000\u0000\u0000\u00b5\u00b0\u0001\u0000\u0000\u0000\u00b6\u00b7\u0001\u0000\u0000\u0000\u00b7\u00b8\u0005\u013e\u0000\u0000\u00b8\u000f\u0001\u0000\u0000\u0000\u00b9\u00ba\u0003*\u0015\u0000\u00ba\u00bb\u0007\u0005\u0000\u0000\u00bb\u00bc\u0005\u00f3\u0000\u0000\u00bc\u0011\u0001\u0000\u0000\u0000\u00bd\u00be\u0003\u001a\r\u0000\u00be\u00bf\u0003N'\u0000\u00bf\u0013\u0001\u0000\u0000\u0000\u00c0\u00c1\u0006\n\uffff\uffff\u0000\u00c1\u00dc\u0005\u010a\u0000\u0000\u00c2\u00dc\u0007\u0006\u0000\u0000\u00c3\u00c4\u0005\u00ef\u0000\u0000\u00c4\u00c5\u0003\u0016\u000b\u0000\u00c5\u00c6\u0005\u00f0\u0000\u0000\u00c6\u00dc\u0001\u0000\u0000\u0000\u00c7\u00ca\u0005\u010a\u0000\u0000\u00c8\u00ca\u00038\u001c\u0000\u00c9\u00c7\u0001\u0000\u0000\u0000\u00c9\u00c8\u0001\u0000\u0000\u0000\u00ca\u00cb\u0001\u0000\u0000\u0000\u00cb\u00d6\u0005\u00ef\u0000\u0000\u00cc\u00d7\u0001\u0000\u0000\u0000\u00cd\u00d7\u0005\u00db\u0000\u0000\u00ce\u00d3\u0003\u0014\n\u0000\u00cf\u00d0\u0005\u00f6\u0000\u0000\u00d0\u00d2\u0003\u0014\n\u0000\u00d1\u00cf\u0001\u0000\u0000\u0000\u00d2\u00d5\u0001\u0000\u0000\u0000\u00d3\u00d1\u0001\u0000\u0000\u0000\u00d3\u00d4\u0001\u0000\u0000\u0000\u00d4\u00d7\u0001\u0000\u0000\u0000\u00d5\u00d3\u0001\u0000\u0000\u0000\u00d6\u00cc\u0001\u0000\u0000\u0000\u00d6\u00cd\u0001\u0000\u0000\u0000\u00d6\u00ce\u0001\u0000\u0000\u0000\u00d7\u00d8\u0001\u0000\u0000\u0000\u00d8\u00dc\u0005\u00f0\u0000\u0000\u00d9\u00da\u0007\u0007\u0000\u0000\u00da\u00dc\u0003\u0014\n\u000e\u00db\u00c0\u0001\u0000\u0000\u0000\u00db\u00c2\u0001\u0000\u0000\u0000\u00db\u00c3\u0001\u0000\u0000\u0000\u00db\u00c9\u0001\u0000\u0000\u0000\u00db\u00d9\u0001\u0000\u0000\u0000\u00dc\u0115\u0001\u0000\u0000\u0000\u00dd\u00de\n\r\u0000\u0000\u00de\u00df\u0007\b\u0000\u0000\u00df\u0114\u0003\u0014\n\u000e\u00e0\u00e1\n\f\u0000\u0000\u00e1\u00e2\u0007\t\u0000\u0000\u00e2\u0114\u0003\u0014\n\r\u00e3\u00e4\n\u000b\u0000\u0000\u00e4\u00e5\u0007\n\u0000\u0000\u00e5\u0114\u0003\u0014\n\f\u00e6\u00e7\n\n\u0000\u0000\u00e7\u00e8\u0007\u000b\u0000\u0000\u00e8\u0114\u0003\u0014\n\u000b\u00e9\u00ea\n\t\u0000\u0000\u00ea\u00eb\u0007\f\u0000\u0000\u00eb\u0114\u0003\u0014\n\n\u00ec\u00ed\n\b\u0000\u0000\u00ed\u00ee\u0005\u0101\u0000\u0000\u00ee\u0114\u0003\u0014\n\t\u00ef\u00f0\n\u0007\u0000\u0000\u00f0\u00f1\u0005\u0103\u0000\u0000\u00f1\u0114\u0003\u0014\n\b\u00f2\u00f3\n\u0006\u0000\u0000\u00f3\u00f4\u0005\u0102\u0000\u0000\u00f4\u0114\u0003\u0014\n\u0007\u00f5\u00f6\n\u0005\u0000\u0000\u00f6\u00f7\u0005\u00e2\u0000\u0000\u00f7\u0114\u0003\u0014\n\u0006\u00f8\u00f9\n\u0004\u0000\u0000\u00f9\u00fa\u0005\u00e3\u0000\u0000\u00fa\u0114\u0003\u0014\n\u0005\u00fb\u00fc\n\u0003\u0000\u0000\u00fc\u00fd\u0005\u00e4\u0000\u0000\u00fd\u0114\u0003\u0014\n\u0004\u00fe\u00ff\n\u0002\u0000\u0000\u00ff\u0100\u0005\u0104\u0000\u0000\u0100\u0101\u0003\u0014\n\u0000\u0101\u0102\u0005\u0001\u0000\u0000\u0102\u0103\u0003\u0014\n\u0002\u0103\u0114\u0001\u0000\u0000\u0000\u0104\u0105\n\u0001\u0000\u0000\u0105\u0106\u0007\r\u0000\u0000\u0106\u0114\u0003\u0014\n\u0001\u0107\u0108\n\u0013\u0000\u0000\u0108\u0109\u0005\u00f4\u0000\u0000\u0109\u010a\u0003\u0016\u000b\u0000\u010a\u010b\u0005\u00f5\u0000\u0000\u010b\u0114\u0001\u0000\u0000\u0000\u010c\u010d\n\u0012\u0000\u0000\u010d\u0114\u0005\u0016\u0000\u0000\u010e\u010f\n\u0010\u0000\u0000\u010f\u0110\u0005\u00f7\u0000\u0000\u0110\u0114\u0005\u010a\u0000\u0000\u0111\u0112\n\u000f\u0000\u0000\u0112\u0114\u0007\u000e\u0000\u0000\u0113\u00dd\u0001\u0000\u0000\u0000\u0113\u00e0\u0001\u0000\u0000\u0000\u0113\u00e3\u0001\u0000\u0000\u0000\u0113\u00e6\u0001\u0000\u0000\u0000\u0113\u00e9\u0001\u0000\u0000\u0000\u0113\u00ec\u0001\u0000\u0000\u0000\u0113\u00ef\u0001\u0000\u0000\u0000\u0113\u00f2\u0001\u0000\u0000\u0000\u0113\u00f5\u0001\u0000\u0000\u0000\u0113\u00f8\u0001\u0000\u0000\u0000\u0113\u00fb\u0001\u0000\u0000\u0000\u0113\u00fe\u0001\u0000\u0000\u0000\u0113\u0104\u0001\u0000\u0000\u0000\u0113\u0107\u0001\u0000\u0000\u0000\u0113\u010c\u0001\u0000\u0000\u0000\u0113\u010e\u0001\u0000\u0000\u0000\u0113\u0111\u0001\u0000\u0000\u0000\u0114\u0117\u0001\u0000\u0000\u0000\u0115\u0113\u0001\u0000\u0000\u0000\u0115\u0116\u0001\u0000\u0000\u0000\u0116\u0015\u0001\u0000\u0000\u0000\u0117\u0115\u0001\u0000\u0000\u0000\u0118\u011d\u0003\u0014\n\u0000\u0119\u011a\u0005\u00f6\u0000\u0000\u011a\u011c\u0003\u0014\n\u0000\u011b\u0119\u0001\u0000\u0000\u0000\u011c\u011f\u0001\u0000\u0000\u0000\u011d\u011b\u0001\u0000\u0000\u0000\u011d\u011e\u0001\u0000\u0000\u0000\u011e\u0017\u0001\u0000\u0000\u0000\u011f\u011d\u0001\u0000\u0000\u0000\u0120\u0121\u0003\u001a\r\u0000\u0121\u0122\u0005\u00f3\u0000\u0000\u0122\u014e\u0001\u0000\u0000\u0000\u0123\u0124\u00036\u001b\u0000\u0124\u0125\u0005\u010a\u0000\u0000\u0125\u012a\u0003D\"\u0000\u0126\u0128\u0005\u010a\u0000\u0000\u0127\u0129\u0003:\u001d\u0000\u0128\u0127\u0001\u0000\u0000\u0000\u0128\u0129\u0001\u0000\u0000\u0000\u0129\u012b\u0001\u0000\u0000\u0000\u012a\u0126\u0001\u0000\u0000\u0000\u012a\u012b\u0001\u0000\u0000\u0000\u012b\u012c\u0001\u0000\u0000\u0000\u012c\u012d\u0005\u00f3\u0000\u0000\u012d\u014e\u0001\u0000\u0000\u0000\u012e\u0137\u00036\u001b\u0000\u012f\u0134\u0005\u010a\u0000\u0000\u0130\u0131\u0005\u00f6\u0000\u0000\u0131\u0133\u0005\u010a\u0000\u0000\u0132\u0130\u0001\u0000\u0000\u0000\u0133\u0136\u0001\u0000\u0000\u0000\u0134\u0132\u0001\u0000\u0000\u0000\u0134\u0135\u0001\u0000\u0000\u0000\u0135\u0138\u0001\u0000\u0000\u0000\u0136\u0134\u0001\u0000\u0000\u0000\u0137\u012f\u0001\u0000\u0000\u0000\u0137\u0138\u0001\u0000\u0000\u0000\u0138\u0139\u0001\u0000\u0000\u0000\u0139\u013a\u0005\u00f3\u0000\u0000\u013a\u014e\u0001\u0000\u0000\u0000\u013b\u013c\u0005\n\u0000\u0000\u013c\u013d\u0003.\u0017\u0000\u013d\u013e\u00038\u001c\u0000\u013e\u013f\u0005\u00f3\u0000\u0000\u013f\u014e\u0001\u0000\u0000\u0000\u0140\u0149\u0003&\u0013\u0000\u0141\u0146\u0003$\u0012\u0000\u0142\u0143\u0005\u00f6\u0000\u0000\u0143\u0145\u0003$\u0012\u0000\u0144\u0142\u0001\u0000\u0000\u0000\u0145\u0148\u0001\u0000\u0000\u0000\u0146\u0144\u0001\u0000\u0000\u0000\u0146\u0147\u0001\u0000\u0000\u0000\u0147\u014a\u0001\u0000\u0000\u0000\u0148\u0146\u0001\u0000\u0000\u0000\u0149\u0141\u0001\u0000\u0000\u0000\u0149\u014a\u0001\u0000\u0000\u0000\u014a\u014b\u0001\u0000\u0000\u0000\u014b\u014c\u0005\u00f3\u0000\u0000\u014c\u014e\u0001\u0000\u0000\u0000\u014d\u0120\u0001\u0000\u0000\u0000\u014d\u0123\u0001\u0000\u0000\u0000\u014d\u012e\u0001\u0000\u0000\u0000\u014d\u013b\u0001\u0000\u0000\u0000\u014d\u0140\u0001\u0000\u0000\u0000\u014e\u0019\u0001\u0000\u0000\u0000\u014f\u0151\u0003 \u0010\u0000\u0150\u014f\u0001\u0000\u0000\u0000\u0150\u0151\u0001\u0000\u0000\u0000\u0151\u0152\u0001\u0000\u0000\u0000\u0152\u0153\u0003&\u0013\u0000\u0153\u0154\u0005\u010a\u0000\u0000\u0154\u0155\u0005\u00ef\u0000\u0000\u0155\u0156\u0003\u001c\u000e\u0000\u0156\u0158\u0005\u00f0\u0000\u0000\u0157\u0159\u0003 \u0010\u0000\u0158\u0157\u0001\u0000\u0000\u0000\u0158\u0159\u0001\u0000\u0000\u0000\u0159\u001b\u0001\u0000\u0000\u0000\u015a\u015f\u0003\u001e\u000f\u0000\u015b\u015c\u0005\u00f6\u0000\u0000\u015c\u015e\u0003\u001e\u000f\u0000\u015d\u015b\u0001\u0000\u0000\u0000\u015e\u0161\u0001\u0000\u0000\u0000\u015f\u015d\u0001\u0000\u0000\u0000\u015f\u0160\u0001\u0000\u0000\u0000\u0160\u0163\u0001\u0000\u0000\u0000\u0161\u015f\u0001\u0000\u0000\u0000\u0162\u015a\u0001\u0000\u0000\u0000\u0162\u0163\u0001\u0000\u0000\u0000\u0163\u001d\u0001\u0000\u0000\u0000\u0164\u0169\u0003&\u0013\u0000\u0165\u0167\u0005\u010a\u0000\u0000\u0166\u0168\u0003:\u001d\u0000\u0167\u0166\u0001\u0000\u0000\u0000\u0167\u0168\u0001\u0000\u0000\u0000\u0168\u016a\u0001\u0000\u0000\u0000\u0169\u0165\u0001\u0000\u0000\u0000\u0169\u016a\u0001\u0000\u0000\u0000\u016a\u001f\u0001\u0000\u0000\u0000\u016b\u016c\u0005\u00f4\u0000\u0000\u016c\u016d\u0005\u00f4\u0000\u0000\u016d\u0172\u0003\"\u0011\u0000\u016e\u016f\u0005\u00f6\u0000\u0000\u016f\u0171\u0003\"\u0011\u0000\u0170\u016e\u0001\u0000\u0000\u0000\u0171\u0174\u0001\u0000\u0000\u0000\u0172\u0170\u0001\u0000\u0000\u0000\u0172\u0173\u0001\u0000\u0000\u0000\u0173\u0175\u0001\u0000\u0000\u0000\u0174\u0172\u0001\u0000\u0000\u0000\u0175\u0176\u0005\u00f5\u0000\u0000\u0176\u0177\u0005\u00f5\u0000\u0000\u0177!\u0001\u0000\u0000\u0000\u0178\u0179\u0005\u010a\u0000\u0000\u0179\u017a\u0005\u0001\u0000\u0000\u017a\u017c\u0005\u0001\u0000\u0000\u017b\u0178\u0001\u0000\u0000\u0000\u017b\u017c\u0001\u0000\u0000\u0000\u017c\u017d\u0001\u0000\u0000\u0000\u017d\u0182\u0005\u010a\u0000\u0000\u017e\u017f\u0005\u00ef\u0000\u0000\u017f\u0180\u0003\u0016\u000b\u0000\u0180\u0181\u0005\u00f0\u0000\u0000\u0181\u0183\u0001\u0000\u0000\u0000\u0182\u017e\u0001\u0000\u0000\u0000\u0182\u0183\u0001\u0000\u0000\u0000\u0183#\u0001\u0000\u0000\u0000\u0184\u0186\u0005\u010a\u0000\u0000\u0185\u0187\u0003:\u001d\u0000\u0186\u0185\u0001\u0000\u0000\u0000\u0186\u0187\u0001\u0000\u0000\u0000\u0187\u018a\u0001\u0000\u0000\u0000\u0188\u0189\u0005\u0105\u0000\u0000\u0189\u018b\u0003J%\u0000\u018a\u0188\u0001\u0000\u0000\u0000\u018a\u018b\u0001\u0000\u0000\u0000\u018b%\u0001\u0000\u0000\u0000\u018c\u018e\u00036\u001b\u0000\u018d\u018c\u0001\u0000\u0000\u0000\u018d\u018e\u0001\u0000\u0000\u0000\u018e\u018f\u0001\u0000\u0000\u0000\u018f\u0190\u00038\u001c\u0000\u0190'\u0001\u0000\u0000\u0000\u0191\u01ba\u0005\u000b\u0000\u0000\u0192\u01ba\u0005\u0004\u0000\u0000\u0193\u01ba\u0005\u0005\u0000\u0000\u0194\u01ba\u0005\u0006\u0000\u0000\u0195\u01ba\u0005\u0010\u0000\u0000\u0196\u01ba\u0005\u0019\u0000\u0000\u0197\u01ba\u0005\u0018\u0000\u0000\u0198\u01ba\u0005\u0002\u0000\u0000\u0199\u01ba\u0005\u0013\u0000\u0000\u019a\u01ba\u0005\u0011\u0000\u0000\u019b\u01ba\u0005\u0003\u0000\u0000\u019c\u01ba\u0005\u0014\u0000\u0000\u019d\u01ba\u0005\u001b\u0000\u0000\u019e\u01ba\u0005\u0012\u0000\u0000\u019f\u01ba\u0005\u001a\u0000\u0000\u01a0\u01ba\u0005\u001c\u0000\u0000\u01a1\u01ba\u0005\u001d\u0000\u0000\u01a2\u01ad\u0005\u001e\u0000\u0000\u01a3\u01a4\u0005\u00ef\u0000\u0000\u01a4\u01a9\u0005\u010a\u0000\u0000\u01a5\u01a6\u0005\u00f6\u0000\u0000\u01a6\u01a8\u0005\u010a\u0000\u0000\u01a7\u01a5\u0001\u0000\u0000\u0000\u01a8\u01ab\u0001\u0000\u0000\u0000\u01a9\u01a7\u0001\u0000\u0000\u0000\u01a9\u01aa\u0001\u0000\u0000\u0000\u01aa\u01ac\u0001\u0000\u0000\u0000\u01ab\u01a9\u0001\u0000\u0000\u0000\u01ac\u01ae\u0005\u00f0\u0000\u0000\u01ad\u01a3\u0001\u0000\u0000\u0000\u01ad\u01ae\u0001\u0000\u0000\u0000\u01ae\u01ba\u0001\u0000\u0000\u0000\u01af\u01ba\u0005\u001f\u0000\u0000\u01b0\u01ba\u0005 \u0000\u0000\u01b1\u01ba\u0005!\u0000\u0000\u01b2\u01ba\u0005\"\u0000\u0000\u01b3\u01ba\u0005#\u0000\u0000\u01b4\u01ba\u0005$\u0000\u0000\u01b5\u01ba\u0005%\u0000\u0000\u01b6\u01ba\u0005&\u0000\u0000\u01b7\u01ba\u0005'\u0000\u0000\u01b8\u01ba\u0005(\u0000\u0000\u01b9\u0191\u0001\u0000\u0000\u0000\u01b9\u0192\u0001\u0000\u0000\u0000\u01b9\u0193\u0001\u0000\u0000\u0000\u01b9\u0194\u0001\u0000\u0000\u0000\u01b9\u0195\u0001\u0000\u0000\u0000\u01b9\u0196\u0001\u0000\u0000\u0000\u01b9\u0197\u0001\u0000\u0000\u0000\u01b9\u0198\u0001\u0000\u0000\u0000\u01b9\u0199\u0001\u0000\u0000\u0000\u01b9\u019a\u0001\u0000\u0000\u0000\u01b9\u019b\u0001\u0000\u0000\u0000\u01b9\u019c\u0001\u0000\u0000\u0000\u01b9\u019d\u0001\u0000\u0000\u0000\u01b9\u019e\u0001\u0000\u0000\u0000\u01b9\u019f\u0001\u0000\u0000\u0000\u01b9\u01a0\u0001\u0000\u0000\u0000\u01b9\u01a1\u0001\u0000\u0000\u0000\u01b9\u01a2\u0001\u0000\u0000\u0000\u01b9\u01af\u0001\u0000\u0000\u0000\u01b9\u01b0\u0001\u0000\u0000\u0000\u01b9\u01b1\u0001\u0000\u0000\u0000\u01b9\u01b2\u0001\u0000\u0000\u0000\u01b9\u01b3\u0001\u0000\u0000\u0000\u01b9\u01b4\u0001\u0000\u0000\u0000\u01b9\u01b5\u0001\u0000\u0000\u0000\u01b9\u01b6\u0001\u0000\u0000\u0000\u01b9\u01b7\u0001\u0000\u0000\u0000\u01b9\u01b8\u0001\u0000\u0000\u0000\u01ba)\u0001\u0000\u0000\u0000\u01bb\u01bc\u0005\u0015\u0000\u0000\u01bc\u01bd\u0005\u00ef\u0000\u0000\u01bd\u01c2\u0003,\u0016\u0000\u01be\u01bf\u0005\u00f6\u0000\u0000\u01bf\u01c1\u0003,\u0016\u0000\u01c0\u01be\u0001\u0000\u0000\u0000\u01c1\u01c4\u0001\u0000\u0000\u0000\u01c2\u01c0\u0001\u0000\u0000\u0000\u01c2\u01c3\u0001\u0000\u0000\u0000\u01c3\u01c5\u0001\u0000\u0000\u0000\u01c4\u01c2\u0001\u0000\u0000\u0000\u01c5\u01c6\u0005\u00f0\u0000\u0000\u01c6+\u0001\u0000\u0000\u0000\u01c7\u01ca\u0005\u010a\u0000\u0000\u01c8\u01c9\u0005\u0105\u0000\u0000\u01c9\u01cb\u0003\u0016\u000b\u0000\u01ca\u01c8\u0001\u0000\u0000\u0000\u01ca\u01cb\u0001\u0000\u0000\u0000\u01cb\u01ce\u0001\u0000\u0000\u0000\u01cc\u01ce\u0005\u0014\u0000\u0000\u01cd\u01c7\u0001\u0000\u0000\u0000\u01cd\u01cc\u0001\u0000\u0000\u0000\u01ce-\u0001\u0000\u0000\u0000\u01cf\u01d0\u0007\u000f\u0000\u0000\u01d0/\u0001\u0000\u0000\u0000\u01d1\u01d2\u0007\u0010\u0000\u0000\u01d21\u0001\u0000\u0000\u0000\u01d3\u01d4\u0005\r\u0000\u0000\u01d43\u0001\u0000\u0000\u0000\u01d5\u01d6\u0005\f\u0000\u0000\u01d65\u0001\u0000\u0000\u0000\u01d7\u01de\u0003(\u0014\u0000\u01d8\u01de\u0003*\u0015\u0000\u01d9\u01de\u0003.\u0017\u0000\u01da\u01de\u00030\u0018\u0000\u01db\u01de\u00032\u0019\u0000\u01dc\u01de\u00034\u001a\u0000\u01dd\u01d7\u0001\u0000\u0000\u0000\u01dd\u01d8\u0001\u0000\u0000\u0000\u01dd\u01d9\u0001\u0000\u0000\u0000\u01dd\u01da\u0001\u0000\u0000\u0000\u01dd\u01db\u0001\u0000\u0000\u0000\u01dd\u01dc\u0001\u0000\u0000\u0000\u01de\u01df\u0001\u0000\u0000\u0000\u01df\u01dd\u0001\u0000\u0000\u0000\u01df\u01e0\u0001\u0000\u0000\u0000\u01e07\u0001\u0000\u0000\u0000\u01e1\u01e6\u0005\u010a\u0000\u0000\u01e2\u01e6\u0003@ \u0000\u01e3\u01e6\u0003>\u001f\u0000\u01e4\u01e6\u0003B!\u0000\u01e5\u01e1\u0001\u0000\u0000\u0000\u01e5\u01e2\u0001\u0000\u0000\u0000\u01e5\u01e3\u0001\u0000\u0000\u0000\u01e5\u01e4\u0001\u0000\u0000\u0000\u01e6\u01e8\u0001\u0000\u0000\u0000\u01e7\u01e9\u0003:\u001d\u0000\u01e8\u01e7\u0001\u0000\u0000\u0000\u01e8\u01e9\u0001\u0000\u0000\u0000\u01e99\u0001\u0000\u0000\u0000\u01ea\u01ec\u0003<\u001e\u0000\u01eb\u01ea\u0001\u0000\u0000\u0000\u01ec\u01ed\u0001\u0000\u0000\u0000\u01ed\u01eb\u0001\u0000\u0000\u0000\u01ed\u01ee\u0001\u0000\u0000\u0000\u01ee;\u0001\u0000\u0000\u0000\u01ef\u01f1\u0005\u00f4\u0000\u0000\u01f0\u01f2\u0003\u0016\u000b\u0000\u01f1\u01f0\u0001\u0000\u0000\u0000\u01f1\u01f2\u0001\u0000\u0000\u0000\u01f2\u01f3\u0001\u0000\u0000\u0000\u01f3\u01f4\u0005\u00f5\u0000\u0000\u01f4=\u0001\u0000\u0000\u0000\u01f5\u01f6\u0007\u0011\u0000\u0000\u01f6?\u0001\u0000\u0000\u0000\u01f7\u01f8\u0007\u0012\u0000\u0000\u01f8A\u0001\u0000\u0000\u0000\u01f9\u01fb\u0005-\u0000\u0000\u01fa\u01fc\u0005\u010a\u0000\u0000\u01fb\u01fa\u0001\u0000\u0000\u0000\u01fb\u01fc\u0001\u0000\u0000\u0000\u01fc\u01fd\u0001\u0000\u0000\u0000\u01fd\u01fe\u0003D\"\u0000\u01feC\u0001\u0000\u0000\u0000\u01ff\u0201\u0005\u00f1\u0000\u0000\u0200\u0202\u0003F#\u0000\u0201\u0200\u0001\u0000\u0000\u0000\u0202\u0203\u0001\u0000\u0000\u0000\u0203\u0201\u0001\u0000\u0000\u0000\u0203\u0204\u0001\u0000\u0000\u0000\u0204\u0205\u0001\u0000\u0000\u0000\u0205\u0206\u0005\u00f2\u0000\u0000\u0206E\u0001\u0000\u0000\u0000\u0207\u0208\u0003&\u0013\u0000\u0208\u020d\u0003H$\u0000\u0209\u020a\u0005\u00f6\u0000\u0000\u020a\u020c\u0003H$\u0000\u020b\u0209\u0001\u0000\u0000\u0000\u020c\u020f\u0001\u0000\u0000\u0000\u020d\u020b\u0001\u0000\u0000\u0000\u020d\u020e\u0001\u0000\u0000\u0000\u020e\u0210\u0001\u0000\u0000\u0000\u020f\u020d\u0001\u0000\u0000\u0000\u0210\u0211\u0005\u00f3\u0000\u0000\u0211G\u0001\u0000\u0000\u0000\u0212\u0214\u0005\u010a\u0000\u0000\u0213\u0215\u0003:\u001d\u0000\u0214\u0213\u0001\u0000\u0000\u0000\u0214\u0215\u0001\u0000\u0000\u0000\u0215I\u0001\u0000\u0000\u0000\u0216\u0227\u0003\u0014\n\u0000\u0217\u0223\u0005\u00f1\u0000\u0000\u0218\u021d\u0003J%\u0000\u0219\u021a\u0005\u00f6\u0000\u0000\u021a\u021c\u0003J%\u0000\u021b\u0219\u0001\u0000\u0000\u0000\u021c\u021f\u0001\u0000\u0000\u0000\u021d\u021b\u0001\u0000\u0000\u0000\u021d\u021e\u0001\u0000\u0000\u0000\u021e\u0221\u0001\u0000\u0000\u0000\u021f\u021d\u0001\u0000\u0000\u0000\u0220\u0222\u0005\u00f6\u0000\u0000\u0221\u0220\u0001\u0000\u0000\u0000\u0221\u0222\u0001\u0000\u0000\u0000\u0222\u0224\u0001\u0000\u0000\u0000\u0223\u0218\u0001\u0000\u0000\u0000\u0223\u0224\u0001\u0000\u0000\u0000\u0224\u0225\u0001\u0000\u0000\u0000\u0225\u0227\u0005\u00f2\u0000\u0000\u0226\u0216\u0001\u0000\u0000\u0000\u0226\u0217\u0001\u0000\u0000\u0000\u0227K\u0001\u0000\u0000\u0000\u0228\u0235\u0003N'\u0000\u0229\u0235\u0003P(\u0000\u022a\u0235\u0003R)\u0000\u022b\u0235\u0003T*\u0000\u022c\u0235\u0003V+\u0000\u022d\u0235\u0003Z-\u0000\u022e\u0235\u0003\\.\u0000\u022f\u0235\u0003b1\u0000\u0230\u0235\u0003^/\u0000\u0231\u0235\u0003`0\u0000\u0232\u0235\u0003d2\u0000\u0233\u0235\u0003f3\u0000\u0234\u0228\u0001\u0000\u0000\u0000\u0234\u0229\u0001\u0000\u0000\u0000\u0234\u022a\u0001\u0000\u0000\u0000\u0234\u022b\u0001\u0000\u0000\u0000\u0234\u022c\u0001\u0000\u0000\u0000\u0234\u022d\u0001\u0000\u0000\u0000\u0234\u022e\u0001\u0000\u0000\u0000\u0234\u022f\u0001\u0000\u0000\u0000\u0234\u0230\u0001\u0000\u0000\u0000\u0234\u0231\u0001\u0000\u0000\u0000\u0234\u0232\u0001\u0000\u0000\u0000\u0234\u0233\u0001\u0000\u0000\u0000\u0235M\u0001\u0000\u0000\u0000\u0236\u023a\u0005\u00f1\u0000\u0000\u0237\u0239\u0003L&\u0000\u0238\u0237\u0001\u0000\u0000\u0000\u0239\u023c\u0001\u0000\u0000\u0000\u023a\u0238\u0001\u0000\u0000\u0000\u023a\u023b\u0001\u0000\u0000\u0000\u023b\u023d\u0001\u0000\u0000\u0000\u023c\u023a\u0001\u0000\u0000\u0000\u023d\u023e\u0005\u00f2\u0000\u0000\u023eO\u0001\u0000\u0000\u0000\u023f\u0240\u0003\u0018\f\u0000\u0240Q\u0001\u0000\u0000\u0000\u0241\u0242\u0003\u0016\u000b\u0000\u0242\u0243\u0005\u00f3\u0000\u0000\u0243S\u0001\u0000\u0000\u0000\u0244\u0245\u0005\u00f3\u0000\u0000\u0245U\u0001\u0000\u0000\u0000\u0246\u0248\u0003 \u0010\u0000\u0247\u0246\u0001\u0000\u0000\u0000\u0247\u0248\u0001\u0000\u0000\u0000\u0248\u0249\u0001\u0000\u0000\u0000\u0249\u024a\u0005.\u0000\u0000\u024a\u024b\u0005\u00ef\u0000\u0000\u024b\u024c\u0003\u0016\u000b\u0000\u024c\u024d\u0005\u00f0\u0000\u0000\u024d\u0250\u0003L&\u0000\u024e\u024f\u0005/\u0000\u0000\u024f\u0251\u0003L&\u0000\u0250\u024e\u0001\u0000\u0000\u0000\u0250\u0251\u0001\u0000\u0000\u0000\u0251W\u0001\u0000\u0000\u0000\u0252\u0253\u0003&\u0013\u0000\u0253\u0254\u0005\u010a\u0000\u0000\u0254\u0255\u0005\u0105\u0000\u0000\u0255\u0256\u0003J%\u0000\u0256Y\u0001\u0000\u0000\u0000\u0257\u0259\u0003 \u0010\u0000\u0258\u0257\u0001\u0000\u0000\u0000\u0258\u0259\u0001\u0000\u0000\u0000\u0259\u025a\u0001\u0000\u0000\u0000\u025a\u025b\u00050\u0000\u0000\u025b\u025c\u0005\u00ef\u0000\u0000\u025c\u025d\u0003\u0016\u000b\u0000\u025d\u025e\u0005\u00f0\u0000\u0000\u025e\u025f\u0003N'\u0000\u025f[\u0001\u0000\u0000\u0000\u0260\u0261\u00051\u0000\u0000\u0261\u0262\u0003\u0016\u000b\u0000\u0262\u0263\u0005\u0001\u0000\u0000\u0263\u0267\u0001\u0000\u0000\u0000\u0264\u0265\u00052\u0000\u0000\u0265\u0267\u0005\u0001\u0000\u0000\u0266\u0260\u0001\u0000\u0000\u0000\u0266\u0264\u0001\u0000\u0000\u0000\u0267]\u0001\u0000\u0000\u0000\u0268\u026a\u0003 \u0010\u0000\u0269\u0268\u0001\u0000\u0000\u0000\u0269\u026a\u0001\u0000\u0000\u0000\u026a\u026b\u0001\u0000\u0000\u0000\u026b\u026c\u00053\u0000\u0000\u026c\u026f\u0005\u00ef\u0000\u0000\u026d\u0270\u0003\u0016\u000b\u0000\u026e\u0270\u0003X,\u0000\u026f\u026d\u0001\u0000\u0000\u0000\u026f\u026e\u0001\u0000\u0000\u0000\u0270\u0271\u0001\u0000\u0000\u0000\u0271\u0272\u0005\u00f0\u0000\u0000\u0272\u0273\u0003L&\u0000\u0273_\u0001\u0000\u0000\u0000\u0274\u0276\u0003 \u0010\u0000\u0275\u0274\u0001\u0000\u0000\u0000\u0275\u0276\u0001\u0000\u0000\u0000\u0276\u0277\u0001\u0000\u0000\u0000\u0277\u0278\u00054\u0000\u0000\u0278\u0279\u0003L&\u0000\u0279\u027a\u00053\u0000\u0000\u027a\u027b\u0005\u00ef\u0000\u0000\u027b\u027c\u0003\u0016\u000b\u0000\u027c\u027d\u0005\u00f0\u0000\u0000\u027d\u027e\u0005\u00f3\u0000\u0000\u027ea\u0001\u0000\u0000\u0000\u027f\u0281\u0003 \u0010\u0000\u0280\u027f\u0001\u0000\u0000\u0000\u0280\u0281\u0001\u0000\u0000\u0000\u0281\u0282\u0001\u0000\u0000\u0000\u0282\u0283\u00055\u0000\u0000\u0283\u0287\u0005\u00ef\u0000\u0000\u0284\u0288\u0003T*\u0000\u0285\u0288\u0003R)\u0000\u0286\u0288\u0003P(\u0000\u0287\u0284\u0001\u0000\u0000\u0000\u0287\u0285\u0001\u0000\u0000\u0000\u0287\u0286\u0001\u0000\u0000\u0000\u0288\u028b\u0001\u0000\u0000\u0000\u0289\u028c\u0003\u0016\u000b\u0000\u028a\u028c\u0003X,\u0000\u028b\u0289\u0001\u0000\u0000\u0000\u028b\u028a\u0001\u0000\u0000\u0000\u028b\u028c\u0001\u0000\u0000\u0000\u028c\u028d\u0001\u0000\u0000\u0000\u028d\u028f\u0005\u00f3\u0000\u0000\u028e\u0290\u0003\u0016\u000b\u0000\u028f\u028e\u0001\u0000\u0000\u0000\u028f\u0290\u0001\u0000\u0000\u0000\u0290\u0291\u0001\u0000\u0000\u0000\u0291\u0292\u0005\u00f0\u0000\u0000\u0292\u0293\u0003L&\u0000\u0293c\u0001\u0000\u0000\u0000\u0294\u0295\u00056\u0000\u0000\u0295\u02a4\u0005\u00f3\u0000\u0000\u0296\u0297\u00057\u0000\u0000\u0297\u02a4\u0005\u00f3\u0000\u0000\u0298\u029a\u00058\u0000\u0000\u0299\u029b\u0003\u0016\u000b\u0000\u029a\u0299\u0001\u0000\u0000\u0000\u029a\u029b\u0001\u0000\u0000\u0000\u029b\u029c\u0001\u0000\u0000\u0000\u029c\u02a4\u0005\u00f3\u0000\u0000\u029d\u029e\u00059\u0000\u0000\u029e\u02a4\u0005\u00f3\u0000\u0000\u029f\u02a0\u0005)\u0000\u0000\u02a0\u02a4\u0005\u00f3\u0000\u0000\u02a1\u02a2\u0005*\u0000\u0000\u02a2\u02a4\u0005\u00f3\u0000\u0000\u02a3\u0294\u0001\u0000\u0000\u0000\u02a3\u0296\u0001\u0000\u0000\u0000\u02a3\u0298\u0001\u0000\u0000\u0000\u02a3\u029d\u0001\u0000\u0000\u0000\u02a3\u029f\u0001\u0000\u0000\u0000\u02a3\u02a1\u0001\u0000\u0000\u0000\u02a4e\u0001\u0000\u0000\u0000\u02a5\u02a6\u0005:\u0000\u0000\u02a6\u02a7\u0005\u00f3\u0000\u0000\u02a7g\u0001\u0000\u0000\u0000Hinw\u0083\u008a\u0095\u009e\u00a5\u00ad\u00b2\u00b5\u00c9\u00d3\u00d6\u00db\u0113\u0115\u011d\u0128\u012a\u0134\u0137\u0146\u0149\u014d\u0150\u0158\u015f\u0162\u0167\u0169\u0172\u017b\u0182\u0186\u018a\u018d\u01a9\u01ad\u01b9\u01c2\u01ca\u01cd\u01dd\u01df\u01e5\u01e8\u01ed\u01f1\u01fb\u0203\u020d\u0214\u021d\u0221\u0223\u0226\u0234\u023a\u0247\u0250\u0258\u0266\u0269\u026f\u0275\u0280\u0287\u028b\u028f\u029a\u02a3";
    public static final ATN _ATN;

    private static String[] makeRuleNames() {
        return new String[]{"translationUnit", "versionStatement", "externalDeclaration", "emptyDeclaration", "pragmaDirective", "extensionDirective", "customDirective", "includeDirective", "layoutDefaults", "functionDefinition", "finiteExpression", "expression", "declaration", "functionPrototype", "functionParameterList", "parameterDeclaration", "attribute", "singleAttribute", "declarationMember", "fullySpecifiedType", "storageQualifier", "layoutQualifier", "layoutQualifierId", "precisionQualifier", "interpolationQualifier", "invariantQualifier", "preciseQualifier", "typeQualifier", "typeSpecifier", "arraySpecifier", "arraySpecifierSegment", "builtinTypeSpecifierParseable", "builtinTypeSpecifierFixed", "structSpecifier", "structBody", "structMember", "structDeclarator", "initializer", "statement", "compoundStatement", "declarationStatement", "expressionStatement", "emptyStatement", "selectionStatement", "iterationCondition", "switchStatement", "caseLabel", "whileStatement", "doWhileStatement", "forStatement", "jumpStatement", "demoteStatement"};
    }

    private static String[] makeLiteralNames() {
        return new String[]{null, null, "'uniform'", "'buffer'", "'in'", "'out'", "'inout'", "'highp'", "'mediump'", "'lowp'", "'precision'", "'const'", "'precise'", null, "'smooth'", "'flat'", "'centroid'", "'attribute'", "'volatile'", "'varying'", "'shared'", "'layout'", "'.length()'", "'noperspective'", "'sample'", "'patch'", "'coherent'", "'restrict'", "'readonly'", "'writeonly'", "'subroutine'", "'devicecoherent'", "'queuefamilycoherent'", "'workgroupcoherent'", "'subgroupcoherent'", "'nonprivate'", "'rayPayloadEXT'", "'rayPayloadInEXT'", "'hitAttributeEXT'", "'callableDataEXT'", "'callableDataInEXT'", "'ignoreIntersectionEXT'", "'terminateRayEXT'", "'accelerationStructureEXT'", "'atomic_uint'", "'struct'", "'if'", "'else'", "'switch'", "'case'", "'default'", "'while'", "'do'", "'for'", "'continue'", "'break'", "'return'", "'discard'", "'demote'", null, null, null, null, null, null, null, null, null, null, "'bool'", "'bvec2'", "'bvec3'", "'bvec4'", "'int8_t'", "'i8vec2'", "'i8vec3'", "'i8vec4'", "'uint8_t'", "'u8vec2'", "'u8vec3'", "'u8vec4'", "'int16_t'", "'i16vec2'", "'i16vec3'", "'i16vec4'", "'uint16_t'", "'u16vec2'", "'u16vec3'", "'u16vec4'", null, null, null, null, null, null, null, null, "'int64_t'", "'i64vec2'", "'i64vec3'", "'i64vec4'", "'uint64_t'", "'u64vec2'", "'u64vec3'", "'u64vec4'", "'float16_t'", "'f16vec2'", "'f16vec3'", "'f16vec4'", null, "'f16mat2x3'", "'f16mat2x4'", "'f16mat3x2'", null, "'f16mat3x4'", "'f16mat4x2'", "'f16mat4x3'", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "'image1D'", "'image2D'", "'image3D'", "'uimage1D'", "'uimage2D'", "'uimage3D'", "'iimage1D'", "'iimage2D'", "'iimage3D'", "'sampler1D'", "'sampler2D'", "'sampler3D'", "'sampler2DRect'", "'sampler1DShadow'", "'sampler2DShadow'", "'sampler2DRectShadow'", "'sampler1DArray'", "'sampler2DArray'", "'sampler1DArrayShadow'", "'sampler2DArrayShadow'", "'isampler1D'", "'isampler2D'", "'isampler2DRect'", "'isampler3D'", "'isampler1DArray'", "'isampler2DArray'", "'usampler1D'", "'usampler2D'", "'usampler2DRect'", "'usampler3D'", "'usampler1DArray'", "'usampler2DArray'", "'sampler2DMS'", "'isampler2DMS'", "'usampler2DMS'", "'sampler2DMSArray'", "'isampler2DMSArray'", "'usampler2DMSArray'", "'image2DRect'", "'image1DArray'", "'image2DArray'", "'image2DMS'", "'image2DMSArray'", "'iimage2DRect'", "'iimage1DArray'", "'iimage2DArray'", "'iimage2DMS'", "'iimage2DMSArray'", "'uimage2DRect'", "'uimage1DArray'", "'uimage2DArray'", "'uimage2DMS'", "'uimage2DMSArray'", "'samplerCubeShadow'", "'samplerCubeArrayShadow'", "'samplerCube'", "'isamplerCube'", "'usamplerCube'", "'samplerBuffer'", "'isamplerBuffer'", "'usamplerBuffer'", "'samplerCubeArray'", "'isamplerCubeArray'", "'usamplerCubeArray'", "'imageCube'", "'uimageCube'", "'iimageCube'", "'imageBuffer'", "'iimageBuffer'", "'uimageBuffer'", "'imageCubeArray'", "'iimageCubeArray'", "'uimageCubeArray'", "'++'", "'--'", "'void'", "'<<'", "'>>'", "'<='", "'>='", "'=='", "'!='", "'&&'", "'^^'", "'||'", "'*='", "'/='", "'%='", "'+='", "'-='", "'<<='", "'>>='", "'&='", "'^='", "'|='", null, null, "'{'", "'}'", "';'", "'['", "']'", "','", "'.'", "'+'", "'-'", "'!'", "'~'", "'*'", "'/'", "'%'", "'<'", null, "'&'", "'|'", "'^'", "'?'", "'='", null, null, null, "'#'", null, null, null, null, null, null, "'extension'", "'version'", null, "'include'", "'pragma'", "'debug'", "'optimize'", null, "'on'", "'off'", "'all'", "'require'", "'enable'", "'warn'", "'disable'", null, null, null, "'STDGL'", "'core'", "'compatibility'", "'es'", "'110'", "'120'", "'100'", "'130'", "'140'", "'150'", "'330'", "'300'", "'310'", "'320'", "'400'", "'410'", "'420'", "'430'", "'440'", "'450'", "'460'", null, null, null, null, null, null, null, null, null, null, "'\"'"};
    }

    private static String[] makeSymbolicNames() {
        return new String[]{null, "COLON", "UNIFORM", "BUFFER", "IN", "OUT", "INOUT", "HIGHP", "MEDIUMP", "LOWP", "PRECISION", "CONST", "PRECISE", "INVARIANT", "SMOOTH", "FLAT", "CENTROID", "ATTRIBUTE", "VOLATILE", "VARYING", "SHARED", "LAYOUT", "DOT_LENGTH_METHOD_CALL", "NOPERSPECTIVE", "SAMPLE", "PATCH", "COHERENT", "RESTRICT", "READONLY", "WRITEONLY", "SUBROUTINE", "DEVICECOHERENT", "QUEUEFAMILYCOHERENT", "WORKGROUPCOHERENT", "SUBGROUPCOHERENT", "NONPRIVATE", "RAY_PAYLOAD_EXT", "RAY_PAYLOAD_IN_EXT", "HIT_ATTRIBUTE_EXT", "CALLABLE_DATA_EXT", "CALLABLE_DATA_IN_EXT", "IGNORE_INTERSECTION_EXT", "TERMINATE_RAY_EXT", "ACCELERATION_STRUCTURE_EXT", "ATOMIC_UINT", "STRUCT", "IF", "ELSE", "SWITCH", "CASE", "DEFAULT", "WHILE", "DO", "FOR", "CONTINUE", "BREAK", "RETURN", "DISCARD", "DEMOTE", "UINT16CONSTANT", "INT16CONSTANT", "UINT32CONSTANT", "INT32CONSTANT", "UINT64CONSTANT", "INT64CONSTANT", "FLOAT16CONSTANT", "FLOAT32CONSTANT", "FLOAT64CONSTANT", "BOOLCONSTANT", "BOOL", "BVEC2", "BVEC3", "BVEC4", "INT8", "I8VEC2", "I8VEC3", "I8VEC4", "UINT8", "U8VEC2", "U8VEC3", "U8VEC4", "INT16", "I16VEC2", "I16VEC3", "I16VEC4", "UINT16", "U16VEC2", "U16VEC3", "U16VEC4", "INT32", "I32VEC2", "I32VEC3", "I32VEC4", "UINT32", "U32VEC2", "U32VEC3", "U32VEC4", "INT64", "I64VEC2", "I64VEC3", "I64VEC4", "UINT64", "U64VEC2", "U64VEC3", "U64VEC4", "FLOAT16", "F16VEC2", "F16VEC3", "F16VEC4", "F16MAT2X2", "F16MAT2X3", "F16MAT2X4", "F16MAT3X2", "F16MAT3X3", "F16MAT3X4", "F16MAT4X2", "F16MAT4X3", "F16MAT4X4", "FLOAT32", "F32VEC2", "F32VEC3", "F32VEC4", "F32MAT2X2", "F32MAT2X3", "F32MAT2X4", "F32MAT3X2", "F32MAT3X3", "F32MAT3X4", "F32MAT4X2", "F32MAT4X3", "F32MAT4X4", "FLOAT64", "F64VEC2", "F64VEC3", "F64VEC4", "F64MAT2X2", "F64MAT2X3", "F64MAT2X4", "F64MAT3X2", "F64MAT3X3", "F64MAT3X4", "F64MAT4X2", "F64MAT4X3", "F64MAT4X4", "IMAGE1D", "IMAGE2D", "IMAGE3D", "UIMAGE1D", "UIMAGE2D", "UIMAGE3D", "IIMAGE1D", "IIMAGE2D", "IIMAGE3D", "SAMPLER1D", "SAMPLER2D", "SAMPLER3D", "SAMPLER2DRECT", "SAMPLER1DSHADOW", "SAMPLER2DSHADOW", "SAMPLER2DRECTSHADOW", "SAMPLER1DARRAY", "SAMPLER2DARRAY", "SAMPLER1DARRAYSHADOW", "SAMPLER2DARRAYSHADOW", "ISAMPLER1D", "ISAMPLER2D", "ISAMPLER2DRECT", "ISAMPLER3D", "ISAMPLER1DARRAY", "ISAMPLER2DARRAY", "USAMPLER1D", "USAMPLER2D", "USAMPLER2DRECT", "USAMPLER3D", "USAMPLER1DARRAY", "USAMPLER2DARRAY", "SAMPLER2DMS", "ISAMPLER2DMS", "USAMPLER2DMS", "SAMPLER2DMSARRAY", "ISAMPLER2DMSARRAY", "USAMPLER2DMSARRAY", "IMAGE2DRECT", "IMAGE1DARRAY", "IMAGE2DARRAY", "IMAGE2DMS", "IMAGE2DMSARRAY", "IIMAGE2DRECT", "IIMAGE1DARRAY", "IIMAGE2DARRAY", "IIMAGE2DMS", "IIMAGE2DMSARRAY", "UIMAGE2DRECT", "UIMAGE1DARRAY", "UIMAGE2DARRAY", "UIMAGE2DMS", "UIMAGE2DMSARRAY", "SAMPLERCUBESHADOW", "SAMPLERCUBEARRAYSHADOW", "SAMPLERCUBE", "ISAMPLERCUBE", "USAMPLERCUBE", "SAMPLERBUFFER", "ISAMPLERBUFFER", "USAMPLERBUFFER", "SAMPLERCUBEARRAY", "ISAMPLERCUBEARRAY", "USAMPLERCUBEARRAY", "IMAGECUBE", "UIMAGECUBE", "IIMAGECUBE", "IMAGEBUFFER", "IIMAGEBUFFER", "UIMAGEBUFFER", "IMAGECUBEARRAY", "IIMAGECUBEARRAY", "UIMAGECUBEARRAY", "INC_OP", "DEC_OP", "VOID", "LEFT_OP", "RIGHT_OP", "LE_OP", "GE_OP", "EQ_OP", "NE_OP", "LOGICAL_AND_OP", "LOGICAL_XOR_OP", "LOGICAL_OR_OP", "MUL_ASSIGN", "DIV_ASSIGN", "MOD_ASSIGN", "ADD_ASSIGN", "SUB_ASSIGN", "LEFT_ASSIGN", "RIGHT_ASSIGN", "AND_ASSIGN", "XOR_ASSIGN", "OR_ASSIGN", "LPAREN", "RPAREN", "LBRACE", "RBRACE", "SEMICOLON", "LBRACKET", "RBRACKET", "COMMA", "DOT", "PLUS_OP", "MINUS_OP", "LOGICAL_NOT_OP", "BITWISE_NEG_OP", "TIMES_OP", "DIV_OP", "MOD_OP", "LT_OP", "GT_OP", "BITWISE_AND_OP", "BITWISE_OR_OP", "BITWISE_XOR_OP", "QUERY_OP", "ASSIGN_OP", "PP_ENTER_MODE", "PP_EMPTY", "NR_LINE", "NR", "IDENTIFIER", "LINE_CONTINUE", "LINE_COMMENT", "BLOCK_COMMENT", "WS", "EOL", "NR_EXTENSION", "NR_VERSION", "NR_CUSTOM", "NR_INCLUDE", "NR_PRAGMA", "NR_PRAGMA_DEBUG", "NR_PRAGMA_OPTIMIZE", "NR_PRAGMA_INVARIANT", "NR_ON", "NR_OFF", "NR_ALL", "NR_REQUIRE", "NR_ENABLE", "NR_WARN", "NR_DISABLE", "NR_COLON", "NR_LPAREN", "NR_RPAREN", "NR_STDGL", "NR_CORE", "NR_COMPATIBILITY", "NR_ES", "NR_GLSL_110", "NR_GLSL_120", "NR_GLSLES_100", "NR_GLSL_130", "NR_GLSL_140", "NR_GLSL_150", "NR_GLSL_330", "NR_GLSLES_300", "NR_GLSLES_310", "NR_GLSLES_320", "NR_GLSL_400", "NR_GLSL_410", "NR_GLSL_420", "NR_GLSL_430", "NR_GLSL_440", "NR_GLSL_450", "NR_GLSL_460", "NR_STRING_START", "NR_STRING_START_ANGLE", "NR_INTCONSTANT", "NR_IDENTIFIER", "NR_LINE_CONTINUE", "NR_LINE_COMMENT", "NR_BLOCK_COMMENT", "NR_EOL", "NR_WS", "S_CONTENT", "S_STRING_END", "S_CONTENT_ANGLE", "S_STRING_END_ANGLE", "C_LINE_COMMENT", "C_BLOCK_COMMENT", "C_EOL", "C_WS", "C_CONTENT", "PP_LINE_CONTINUE", "PP_LINE_COMMENT", "PP_BLOCK_COMMENT", "PP_EOL", "PP_CONTENT"};
    }

    @Override
    @Deprecated
    public String[] getTokenNames() {
        return tokenNames;
    }

    @Override
    public Vocabulary getVocabulary() {
        return VOCABULARY;
    }

    @Override
    public String getGrammarFileName() {
        return "java-escape";
    }

    @Override
    public String[] getRuleNames() {
        return ruleNames;
    }

    @Override
    public String getSerializedATN() {
        return _serializedATN;
    }

    @Override
    public ATN getATN() {
        return _ATN;
    }

    public GLSLParser(TokenStream input) {
        super(input);
        this._interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final TranslationUnitContext translationUnit() throws RecognitionException {
        TranslationUnitContext _localctx = new TranslationUnitContext(this._ctx, this.getState());
        this.enterRule(_localctx, 0, 0);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(105);
            this._errHandler.sync(this);
            switch (((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 0, this._ctx)) {
                case 1: {
                    this.setState(104);
                    this.versionStatement();
                }
            }
            this.setState(110);
            this._errHandler.sync(this);
            int _la = this._input.LA(1);
            while ((_la & 0xFFFFFFC0) == 0 && (1L << _la & 0x39FFFFBFFFFCL) != 0L || (_la - 69 & 0xFFFFFFC0) == 0 && (1L << _la - 69 & 0xFFFFFFFFFFFFFFFFL) != 0L || (_la - 133 & 0xFFFFFFC0) == 0 && (1L << _la - 133 & 0xFFFFFFFFFFFFFFFFL) != 0L || (_la - 197 & 0xFFFFFFC0) == 0 && (1L << _la - 197 & 0xC000004FFFFFL) != 0L || _la == 265 || _la == 266) {
                this.setState(107);
                this.externalDeclaration();
                this.setState(112);
                this._errHandler.sync(this);
                _la = this._input.LA(1);
            }
            this.setState(113);
            this.match(-1);
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final VersionStatementContext versionStatement() throws RecognitionException {
        VersionStatementContext _localctx = new VersionStatementContext(this._ctx, this.getState());
        this.enterRule(_localctx, 2, 1);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(115);
            this.match(265);
            this.setState(116);
            this.match(273);
            this.setState(117);
            _localctx.version = this._input.LT(1);
            int _la = this._input.LA(1);
            if ((_la - 294 & 0xFFFFFFC0) != 0 || (1L << _la - 294 & 0x1FFFFL) == 0L) {
                _localctx.version = this._errHandler.recoverInline(this);
            } else {
                if (this._input.LA(1) == -1) {
                    this.matchedEOF = true;
                }
                this._errHandler.reportMatch(this);
                this.consume();
            }
            this.setState(119);
            this._errHandler.sync(this);
            _la = this._input.LA(1);
            if ((_la - 291 & 0xFFFFFFC0) == 0 && (1L << _la - 291 & 7L) != 0L) {
                this.setState(118);
                _localctx.profile = this._input.LT(1);
                _la = this._input.LA(1);
                if ((_la - 291 & 0xFFFFFFC0) != 0 || (1L << _la - 291 & 7L) == 0L) {
                    _localctx.profile = this._errHandler.recoverInline(this);
                } else {
                    if (this._input.LA(1) == -1) {
                        this.matchedEOF = true;
                    }
                    this._errHandler.reportMatch(this);
                    this.consume();
                }
            }
            this.setState(121);
            this.match(318);
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public final ExternalDeclarationContext externalDeclaration() throws RecognitionException {
        ExternalDeclarationContext _localctx = new ExternalDeclarationContext(this._ctx, this.getState());
        this.enterRule(_localctx, 4, 2);
        try {
            this.setState(131);
            this._errHandler.sync(this);
            switch (((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 3, this._ctx)) {
                case 1: {
                    this.enterOuterAlt(_localctx, 1);
                    this.setState(123);
                    this.functionDefinition();
                    return _localctx;
                }
                case 2: {
                    this.enterOuterAlt(_localctx, 2);
                    this.setState(124);
                    this.declaration();
                    return _localctx;
                }
                case 3: {
                    this.enterOuterAlt(_localctx, 3);
                    this.setState(125);
                    this.pragmaDirective();
                    return _localctx;
                }
                case 4: {
                    this.enterOuterAlt(_localctx, 4);
                    this.setState(126);
                    this.extensionDirective();
                    return _localctx;
                }
                case 5: {
                    this.enterOuterAlt(_localctx, 5);
                    this.setState(127);
                    this.customDirective();
                    return _localctx;
                }
                case 6: {
                    this.enterOuterAlt(_localctx, 6);
                    this.setState(128);
                    this.includeDirective();
                    return _localctx;
                }
                case 7: {
                    this.enterOuterAlt(_localctx, 7);
                    this.setState(129);
                    this.layoutDefaults();
                    return _localctx;
                }
                case 8: {
                    this.enterOuterAlt(_localctx, 8);
                    this.setState(130);
                    this.emptyDeclaration();
                    return _localctx;
                }
            }
            return _localctx;
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
            return _localctx;
        }
        finally {
            this.exitRule();
        }
    }

    public final EmptyDeclarationContext emptyDeclaration() throws RecognitionException {
        EmptyDeclarationContext _localctx = new EmptyDeclarationContext(this._ctx, this.getState());
        this.enterRule(_localctx, 6, 3);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(133);
            this.match(243);
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final PragmaDirectiveContext pragmaDirective() throws RecognitionException {
        PragmaDirectiveContext _localctx = new PragmaDirectiveContext(this._ctx, this.getState());
        this.enterRule(_localctx, 8, 4);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(135);
            this.match(265);
            this.setState(136);
            this.match(276);
            this.setState(138);
            this._errHandler.sync(this);
            int _la = this._input.LA(1);
            if (_la == 290) {
                this.setState(137);
                _localctx.stdGL = this.match(290);
            }
            this.setState(149);
            this._errHandler.sync(this);
            switch (this._input.LA(1)) {
                case 314: {
                    this.setState(140);
                    _localctx.type = this.match(314);
                    break;
                }
                case 277: 
                case 278: {
                    this.setState(141);
                    _localctx.type = this._input.LT(1);
                    _la = this._input.LA(1);
                    if (_la != 277 && _la != 278) {
                        _localctx.type = this._errHandler.recoverInline(this);
                    } else {
                        if (this._input.LA(1) == -1) {
                            this.matchedEOF = true;
                        }
                        this._errHandler.reportMatch(this);
                        this.consume();
                    }
                    this.setState(142);
                    this.match(288);
                    this.setState(143);
                    _localctx.state = this._input.LT(1);
                    _la = this._input.LA(1);
                    if (_la != 280 && _la != 281) {
                        _localctx.state = this._errHandler.recoverInline(this);
                    } else {
                        if (this._input.LA(1) == -1) {
                            this.matchedEOF = true;
                        }
                        this._errHandler.reportMatch(this);
                        this.consume();
                    }
                    this.setState(144);
                    this.match(289);
                    break;
                }
                case 279: {
                    this.setState(145);
                    _localctx.type = this.match(279);
                    this.setState(146);
                    this.match(288);
                    this.setState(147);
                    _localctx.state = this.match(282);
                    this.setState(148);
                    this.match(289);
                    break;
                }
                default: {
                    throw new NoViableAltException(this);
                }
            }
            this.setState(151);
            this.match(318);
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final ExtensionDirectiveContext extensionDirective() throws RecognitionException {
        ExtensionDirectiveContext _localctx = new ExtensionDirectiveContext(this._ctx, this.getState());
        this.enterRule(_localctx, 10, 5);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(153);
            this.match(265);
            this.setState(154);
            this.match(272);
            this.setState(155);
            _localctx.extensionName = this.match(314);
            this.setState(158);
            this._errHandler.sync(this);
            int _la = this._input.LA(1);
            if (_la == 287) {
                this.setState(156);
                this.match(287);
                this.setState(157);
                _localctx.extensionBehavior = this._input.LT(1);
                _la = this._input.LA(1);
                if ((_la - 283 & 0xFFFFFFC0) != 0 || (1L << _la - 283 & 0xFL) == 0L) {
                    _localctx.extensionBehavior = this._errHandler.recoverInline(this);
                } else {
                    if (this._input.LA(1) == -1) {
                        this.matchedEOF = true;
                    }
                    this._errHandler.reportMatch(this);
                    this.consume();
                }
            }
            this.setState(160);
            this.match(318);
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final CustomDirectiveContext customDirective() throws RecognitionException {
        CustomDirectiveContext _localctx = new CustomDirectiveContext(this._ctx, this.getState());
        this.enterRule(_localctx, 12, 6);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(162);
            this.match(265);
            this.setState(163);
            this.match(274);
            this.setState(165);
            this._errHandler.sync(this);
            int _la = this._input.LA(1);
            if (_la == 328) {
                this.setState(164);
                _localctx.content = this.match(328);
            }
            this.setState(167);
            this.match(326);
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final IncludeDirectiveContext includeDirective() throws RecognitionException {
        IncludeDirectiveContext _localctx = new IncludeDirectiveContext(this._ctx, this.getState());
        this.enterRule(_localctx, 14, 7);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(169);
            this.match(265);
            this.setState(170);
            this.match(275);
            this.setState(181);
            this._errHandler.sync(this);
            switch (this._input.LA(1)) {
                case 311: {
                    this.setState(171);
                    this.match(311);
                    this.setState(173);
                    this._errHandler.sync(this);
                    int _la = this._input.LA(1);
                    if (_la == 320) {
                        this.setState(172);
                        _localctx.content = this.match(320);
                    }
                    this.setState(175);
                    this.match(321);
                    break;
                }
                case 312: {
                    this.setState(176);
                    _localctx.angleStart = this.match(312);
                    this.setState(178);
                    this._errHandler.sync(this);
                    int _la = this._input.LA(1);
                    if (_la == 322) {
                        this.setState(177);
                        _localctx.content = this.match(322);
                    }
                    this.setState(180);
                    this.match(323);
                    break;
                }
                default: {
                    throw new NoViableAltException(this);
                }
            }
            this.setState(183);
            this.match(318);
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final LayoutDefaultsContext layoutDefaults() throws RecognitionException {
        LayoutDefaultsContext _localctx = new LayoutDefaultsContext(this._ctx, this.getState());
        this.enterRule(_localctx, 16, 8);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(185);
            this.layoutQualifier();
            this.setState(186);
            _localctx.layoutMode = this._input.LT(1);
            int _la = this._input.LA(1);
            if ((_la & 0xFFFFFFC0) != 0 || (1L << _la & 0x3CL) == 0L) {
                _localctx.layoutMode = this._errHandler.recoverInline(this);
            } else {
                if (this._input.LA(1) == -1) {
                    this.matchedEOF = true;
                }
                this._errHandler.reportMatch(this);
                this.consume();
            }
            this.setState(187);
            this.match(243);
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    public final FunctionDefinitionContext functionDefinition() throws RecognitionException {
        FunctionDefinitionContext _localctx = new FunctionDefinitionContext(this._ctx, this.getState());
        this.enterRule(_localctx, 18, 9);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(189);
            this.functionPrototype();
            this.setState(190);
            this.compoundStatement();
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    public final FiniteExpressionContext finiteExpression() throws RecognitionException {
        return this.finiteExpression(0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private FiniteExpressionContext finiteExpression(int _p) throws RecognitionException {
        FiniteExpressionContext _localctx;
        ParserRuleContext _parentctx = this._ctx;
        int _parentState = this.getState();
        FiniteExpressionContext _prevctx = _localctx = new FiniteExpressionContext(this._ctx, _parentState);
        int _startState = 20;
        this.enterRecursionRule(_localctx, 20, 10, _p);
        try {
            int _la;
            this.enterOuterAlt(_localctx, 1);
            this.setState(219);
            this._errHandler.sync(this);
            switch (((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 14, this._ctx)) {
                case 1: {
                    _localctx = new ReferenceExpressionContext(_localctx);
                    this._ctx = _localctx;
                    _prevctx = _localctx;
                    this.setState(193);
                    this.match(266);
                    break;
                }
                case 2: {
                    _localctx = new LiteralExpressionContext(_localctx);
                    this._ctx = _localctx;
                    _prevctx = _localctx;
                    this.setState(194);
                    _la = this._input.LA(1);
                    if ((_la - 59 & 0xFFFFFFC0) != 0 || (1L << _la - 59 & 0x3FFL) == 0L) {
                        this._errHandler.recoverInline(this);
                        break;
                    }
                    if (this._input.LA(1) == -1) {
                        this.matchedEOF = true;
                    }
                    this._errHandler.reportMatch(this);
                    this.consume();
                    break;
                }
                case 3: {
                    _localctx = new GroupingExpressionContext(_localctx);
                    this._ctx = _localctx;
                    _prevctx = _localctx;
                    this.setState(195);
                    this.match(239);
                    this.setState(196);
                    ((GroupingExpressionContext)_localctx).value = this.expression();
                    this.setState(197);
                    this.match(240);
                    break;
                }
                case 4: {
                    _localctx = new FunctionCallExpressionContext(_localctx);
                    this._ctx = _localctx;
                    _prevctx = _localctx;
                    this.setState(201);
                    this._errHandler.sync(this);
                    switch (((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 11, this._ctx)) {
                        case 1: {
                            this.setState(199);
                            this.match(266);
                            break;
                        }
                        case 2: {
                            this.setState(200);
                            this.typeSpecifier();
                        }
                    }
                    this.setState(203);
                    this.match(239);
                    this.setState(214);
                    this._errHandler.sync(this);
                    switch (((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 13, this._ctx)) {
                        case 1: {
                            break;
                        }
                        case 2: {
                            this.setState(205);
                            this.match(219);
                            break;
                        }
                        case 3: {
                            this.setState(206);
                            ((FunctionCallExpressionContext)_localctx).finiteExpression = this.finiteExpression(0);
                            ((FunctionCallExpressionContext)_localctx).parameters.add(((FunctionCallExpressionContext)_localctx).finiteExpression);
                            this.setState(211);
                            this._errHandler.sync(this);
                            _la = this._input.LA(1);
                            while (_la == 246) {
                                this.setState(207);
                                this.match(246);
                                this.setState(208);
                                ((FunctionCallExpressionContext)_localctx).finiteExpression = this.finiteExpression(0);
                                ((FunctionCallExpressionContext)_localctx).parameters.add(((FunctionCallExpressionContext)_localctx).finiteExpression);
                                this.setState(213);
                                this._errHandler.sync(this);
                                _la = this._input.LA(1);
                            }
                            break;
                        }
                    }
                    this.setState(216);
                    this.match(240);
                    break;
                }
                case 5: {
                    _localctx = new PrefixExpressionContext(_localctx);
                    this._ctx = _localctx;
                    _prevctx = _localctx;
                    this.setState(217);
                    ((PrefixExpressionContext)_localctx).op = this._input.LT(1);
                    _la = this._input.LA(1);
                    if ((_la - 217 & 0xFFFFFFC0) != 0 || (1L << _la - 217 & 0x780000003L) == 0L) {
                        ((PrefixExpressionContext)_localctx).op = this._errHandler.recoverInline(this);
                    } else {
                        if (this._input.LA(1) == -1) {
                            this.matchedEOF = true;
                        }
                        this._errHandler.reportMatch(this);
                        this.consume();
                    }
                    this.setState(218);
                    ((PrefixExpressionContext)_localctx).operand = this.finiteExpression(14);
                }
            }
            this._ctx.stop = this._input.LT(-1);
            this.setState(277);
            this._errHandler.sync(this);
            int _alt = ((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 16, this._ctx);
            while (_alt != 2 && _alt != 0) {
                if (_alt == 1) {
                    if (this._parseListeners != null) {
                        this.triggerExitRuleEvent();
                    }
                    _prevctx = _localctx;
                    this.setState(275);
                    this._errHandler.sync(this);
                    switch (((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 15, this._ctx)) {
                        case 1: {
                            _localctx = new MultiplicativeExpressionContext(new FiniteExpressionContext(_parentctx, _parentState));
                            ((MultiplicativeExpressionContext)_localctx).left = _prevctx;
                            this.pushNewRecursionContext(_localctx, _startState, 10);
                            this.setState(221);
                            if (!this.precpred(this._ctx, 13)) {
                                throw new FailedPredicateException(this, "precpred(_ctx, 13)");
                            }
                            this.setState(222);
                            ((MultiplicativeExpressionContext)_localctx).op = this._input.LT(1);
                            _la = this._input.LA(1);
                            if ((_la - 252 & 0xFFFFFFC0) != 0 || (1L << _la - 252 & 7L) == 0L) {
                                ((MultiplicativeExpressionContext)_localctx).op = this._errHandler.recoverInline(this);
                            } else {
                                if (this._input.LA(1) == -1) {
                                    this.matchedEOF = true;
                                }
                                this._errHandler.reportMatch(this);
                                this.consume();
                            }
                            this.setState(223);
                            ((MultiplicativeExpressionContext)_localctx).right = this.finiteExpression(14);
                            break;
                        }
                        case 2: {
                            _localctx = new AdditiveExpressionContext(new FiniteExpressionContext(_parentctx, _parentState));
                            ((AdditiveExpressionContext)_localctx).left = _prevctx;
                            this.pushNewRecursionContext(_localctx, _startState, 10);
                            this.setState(224);
                            if (!this.precpred(this._ctx, 12)) {
                                throw new FailedPredicateException(this, "precpred(_ctx, 12)");
                            }
                            this.setState(225);
                            ((AdditiveExpressionContext)_localctx).op = this._input.LT(1);
                            _la = this._input.LA(1);
                            if (_la != 248 && _la != 249) {
                                ((AdditiveExpressionContext)_localctx).op = this._errHandler.recoverInline(this);
                            } else {
                                if (this._input.LA(1) == -1) {
                                    this.matchedEOF = true;
                                }
                                this._errHandler.reportMatch(this);
                                this.consume();
                            }
                            this.setState(226);
                            ((AdditiveExpressionContext)_localctx).right = this.finiteExpression(13);
                            break;
                        }
                        case 3: {
                            _localctx = new ShiftExpressionContext(new FiniteExpressionContext(_parentctx, _parentState));
                            ((ShiftExpressionContext)_localctx).left = _prevctx;
                            this.pushNewRecursionContext(_localctx, _startState, 10);
                            this.setState(227);
                            if (!this.precpred(this._ctx, 11)) {
                                throw new FailedPredicateException(this, "precpred(_ctx, 11)");
                            }
                            this.setState(228);
                            ((ShiftExpressionContext)_localctx).op = this._input.LT(1);
                            _la = this._input.LA(1);
                            if (_la != 220 && _la != 221) {
                                ((ShiftExpressionContext)_localctx).op = this._errHandler.recoverInline(this);
                            } else {
                                if (this._input.LA(1) == -1) {
                                    this.matchedEOF = true;
                                }
                                this._errHandler.reportMatch(this);
                                this.consume();
                            }
                            this.setState(229);
                            ((ShiftExpressionContext)_localctx).right = this.finiteExpression(12);
                            break;
                        }
                        case 4: {
                            _localctx = new RelationalExpressionContext(new FiniteExpressionContext(_parentctx, _parentState));
                            ((RelationalExpressionContext)_localctx).left = _prevctx;
                            this.pushNewRecursionContext(_localctx, _startState, 10);
                            this.setState(230);
                            if (!this.precpred(this._ctx, 10)) {
                                throw new FailedPredicateException(this, "precpred(_ctx, 10)");
                            }
                            this.setState(231);
                            ((RelationalExpressionContext)_localctx).op = this._input.LT(1);
                            _la = this._input.LA(1);
                            if ((_la - 222 & 0xFFFFFFC0) != 0 || (1L << _la - 222 & 0x600000003L) == 0L) {
                                ((RelationalExpressionContext)_localctx).op = this._errHandler.recoverInline(this);
                            } else {
                                if (this._input.LA(1) == -1) {
                                    this.matchedEOF = true;
                                }
                                this._errHandler.reportMatch(this);
                                this.consume();
                            }
                            this.setState(232);
                            ((RelationalExpressionContext)_localctx).right = this.finiteExpression(11);
                            break;
                        }
                        case 5: {
                            _localctx = new EqualityExpressionContext(new FiniteExpressionContext(_parentctx, _parentState));
                            ((EqualityExpressionContext)_localctx).left = _prevctx;
                            this.pushNewRecursionContext(_localctx, _startState, 10);
                            this.setState(233);
                            if (!this.precpred(this._ctx, 9)) {
                                throw new FailedPredicateException(this, "precpred(_ctx, 9)");
                            }
                            this.setState(234);
                            ((EqualityExpressionContext)_localctx).op = this._input.LT(1);
                            _la = this._input.LA(1);
                            if (_la != 224 && _la != 225) {
                                ((EqualityExpressionContext)_localctx).op = this._errHandler.recoverInline(this);
                            } else {
                                if (this._input.LA(1) == -1) {
                                    this.matchedEOF = true;
                                }
                                this._errHandler.reportMatch(this);
                                this.consume();
                            }
                            this.setState(235);
                            ((EqualityExpressionContext)_localctx).right = this.finiteExpression(10);
                            break;
                        }
                        case 6: {
                            _localctx = new BitwiseAndExpressionContext(new FiniteExpressionContext(_parentctx, _parentState));
                            ((BitwiseAndExpressionContext)_localctx).left = _prevctx;
                            this.pushNewRecursionContext(_localctx, _startState, 10);
                            this.setState(236);
                            if (!this.precpred(this._ctx, 8)) {
                                throw new FailedPredicateException(this, "precpred(_ctx, 8)");
                            }
                            this.setState(237);
                            ((BitwiseAndExpressionContext)_localctx).op = this.match(257);
                            this.setState(238);
                            ((BitwiseAndExpressionContext)_localctx).right = this.finiteExpression(9);
                            break;
                        }
                        case 7: {
                            _localctx = new BitwiseExclusiveOrExpressionContext(new FiniteExpressionContext(_parentctx, _parentState));
                            ((BitwiseExclusiveOrExpressionContext)_localctx).left = _prevctx;
                            this.pushNewRecursionContext(_localctx, _startState, 10);
                            this.setState(239);
                            if (!this.precpred(this._ctx, 7)) {
                                throw new FailedPredicateException(this, "precpred(_ctx, 7)");
                            }
                            this.setState(240);
                            ((BitwiseExclusiveOrExpressionContext)_localctx).op = this.match(259);
                            this.setState(241);
                            ((BitwiseExclusiveOrExpressionContext)_localctx).right = this.finiteExpression(8);
                            break;
                        }
                        case 8: {
                            _localctx = new BitwiseInclusiveOrExpressionContext(new FiniteExpressionContext(_parentctx, _parentState));
                            ((BitwiseInclusiveOrExpressionContext)_localctx).left = _prevctx;
                            this.pushNewRecursionContext(_localctx, _startState, 10);
                            this.setState(242);
                            if (!this.precpred(this._ctx, 6)) {
                                throw new FailedPredicateException(this, "precpred(_ctx, 6)");
                            }
                            this.setState(243);
                            ((BitwiseInclusiveOrExpressionContext)_localctx).op = this.match(258);
                            this.setState(244);
                            ((BitwiseInclusiveOrExpressionContext)_localctx).right = this.finiteExpression(7);
                            break;
                        }
                        case 9: {
                            _localctx = new LogicalAndExpressionContext(new FiniteExpressionContext(_parentctx, _parentState));
                            ((LogicalAndExpressionContext)_localctx).left = _prevctx;
                            this.pushNewRecursionContext(_localctx, _startState, 10);
                            this.setState(245);
                            if (!this.precpred(this._ctx, 5)) {
                                throw new FailedPredicateException(this, "precpred(_ctx, 5)");
                            }
                            this.setState(246);
                            ((LogicalAndExpressionContext)_localctx).op = this.match(226);
                            this.setState(247);
                            ((LogicalAndExpressionContext)_localctx).right = this.finiteExpression(6);
                            break;
                        }
                        case 10: {
                            _localctx = new LogicalExclusiveOrExpressionContext(new FiniteExpressionContext(_parentctx, _parentState));
                            ((LogicalExclusiveOrExpressionContext)_localctx).left = _prevctx;
                            this.pushNewRecursionContext(_localctx, _startState, 10);
                            this.setState(248);
                            if (!this.precpred(this._ctx, 4)) {
                                throw new FailedPredicateException(this, "precpred(_ctx, 4)");
                            }
                            this.setState(249);
                            ((LogicalExclusiveOrExpressionContext)_localctx).op = this.match(227);
                            this.setState(250);
                            ((LogicalExclusiveOrExpressionContext)_localctx).right = this.finiteExpression(5);
                            break;
                        }
                        case 11: {
                            _localctx = new LogicalInclusiveOrExpressionContext(new FiniteExpressionContext(_parentctx, _parentState));
                            ((LogicalInclusiveOrExpressionContext)_localctx).left = _prevctx;
                            this.pushNewRecursionContext(_localctx, _startState, 10);
                            this.setState(251);
                            if (!this.precpred(this._ctx, 3)) {
                                throw new FailedPredicateException(this, "precpred(_ctx, 3)");
                            }
                            this.setState(252);
                            ((LogicalInclusiveOrExpressionContext)_localctx).op = this.match(228);
                            this.setState(253);
                            ((LogicalInclusiveOrExpressionContext)_localctx).right = this.finiteExpression(4);
                            break;
                        }
                        case 12: {
                            _localctx = new ConditionalExpressionContext(new FiniteExpressionContext(_parentctx, _parentState));
                            ((ConditionalExpressionContext)_localctx).condition = _prevctx;
                            this.pushNewRecursionContext(_localctx, _startState, 10);
                            this.setState(254);
                            if (!this.precpred(this._ctx, 2)) {
                                throw new FailedPredicateException(this, "precpred(_ctx, 2)");
                            }
                            this.setState(255);
                            this.match(260);
                            this.setState(256);
                            ((ConditionalExpressionContext)_localctx).trueAlternative = this.finiteExpression(0);
                            this.setState(257);
                            this.match(1);
                            this.setState(258);
                            ((ConditionalExpressionContext)_localctx).falseAlternative = this.finiteExpression(2);
                            break;
                        }
                        case 13: {
                            _localctx = new AssignmentExpressionContext(new FiniteExpressionContext(_parentctx, _parentState));
                            ((AssignmentExpressionContext)_localctx).left = _prevctx;
                            this.pushNewRecursionContext(_localctx, _startState, 10);
                            this.setState(260);
                            if (!this.precpred(this._ctx, 1)) {
                                throw new FailedPredicateException(this, "precpred(_ctx, 1)");
                            }
                            this.setState(261);
                            ((AssignmentExpressionContext)_localctx).op = this._input.LT(1);
                            _la = this._input.LA(1);
                            if ((_la - 229 & 0xFFFFFFC0) != 0 || (1L << _la - 229 & 0x1000003FFL) == 0L) {
                                ((AssignmentExpressionContext)_localctx).op = this._errHandler.recoverInline(this);
                            } else {
                                if (this._input.LA(1) == -1) {
                                    this.matchedEOF = true;
                                }
                                this._errHandler.reportMatch(this);
                                this.consume();
                            }
                            this.setState(262);
                            ((AssignmentExpressionContext)_localctx).right = this.finiteExpression(1);
                            break;
                        }
                        case 14: {
                            _localctx = new ArrayAccessExpressionContext(new FiniteExpressionContext(_parentctx, _parentState));
                            ((ArrayAccessExpressionContext)_localctx).left = _prevctx;
                            this.pushNewRecursionContext(_localctx, _startState, 10);
                            this.setState(263);
                            if (!this.precpred(this._ctx, 19)) {
                                throw new FailedPredicateException(this, "precpred(_ctx, 19)");
                            }
                            this.setState(264);
                            this.match(244);
                            this.setState(265);
                            ((ArrayAccessExpressionContext)_localctx).right = this.expression();
                            this.setState(266);
                            this.match(245);
                            break;
                        }
                        case 15: {
                            _localctx = new LengthAccessExpressionContext(new FiniteExpressionContext(_parentctx, _parentState));
                            ((LengthAccessExpressionContext)_localctx).operand = _prevctx;
                            this.pushNewRecursionContext(_localctx, _startState, 10);
                            this.setState(268);
                            if (!this.precpred(this._ctx, 18)) {
                                throw new FailedPredicateException(this, "precpred(_ctx, 18)");
                            }
                            this.setState(269);
                            this.match(22);
                            break;
                        }
                        case 16: {
                            _localctx = new MemberAccessExpressionContext(new FiniteExpressionContext(_parentctx, _parentState));
                            ((MemberAccessExpressionContext)_localctx).operand = _prevctx;
                            this.pushNewRecursionContext(_localctx, _startState, 10);
                            this.setState(270);
                            if (!this.precpred(this._ctx, 16)) {
                                throw new FailedPredicateException(this, "precpred(_ctx, 16)");
                            }
                            this.setState(271);
                            this.match(247);
                            this.setState(272);
                            ((MemberAccessExpressionContext)_localctx).member = this.match(266);
                            break;
                        }
                        case 17: {
                            _localctx = new PostfixExpressionContext(new FiniteExpressionContext(_parentctx, _parentState));
                            ((PostfixExpressionContext)_localctx).operand = _prevctx;
                            this.pushNewRecursionContext(_localctx, _startState, 10);
                            this.setState(273);
                            if (!this.precpred(this._ctx, 15)) {
                                throw new FailedPredicateException(this, "precpred(_ctx, 15)");
                            }
                            this.setState(274);
                            ((PostfixExpressionContext)_localctx).op = this._input.LT(1);
                            _la = this._input.LA(1);
                            if (_la != 217 && _la != 218) {
                                ((PostfixExpressionContext)_localctx).op = this._errHandler.recoverInline(this);
                                break;
                            }
                            if (this._input.LA(1) == -1) {
                                this.matchedEOF = true;
                            }
                            this._errHandler.reportMatch(this);
                            this.consume();
                        }
                    }
                }
                this.setState(279);
                this._errHandler.sync(this);
                _alt = ((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 16, this._ctx);
            }
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.unrollRecursionContexts(_parentctx);
        }
        return _localctx;
    }

    public final ExpressionContext expression() throws RecognitionException {
        ExpressionContext _localctx = new ExpressionContext(this._ctx, this.getState());
        this.enterRule(_localctx, 22, 11);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(280);
            _localctx.finiteExpression = this.finiteExpression(0);
            _localctx.items.add(_localctx.finiteExpression);
            this.setState(285);
            this._errHandler.sync(this);
            int _alt = ((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 17, this._ctx);
            while (_alt != 2 && _alt != 0) {
                if (_alt == 1) {
                    this.setState(281);
                    this.match(246);
                    this.setState(282);
                    _localctx.finiteExpression = this.finiteExpression(0);
                    _localctx.items.add(_localctx.finiteExpression);
                }
                this.setState(287);
                this._errHandler.sync(this);
                _alt = ((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 17, this._ctx);
            }
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public final DeclarationContext declaration() throws RecognitionException {
        DeclarationContext _localctx = new DeclarationContext(this._ctx, this.getState());
        this.enterRule(_localctx, 24, 12);
        try {
            this.setState(333);
            this._errHandler.sync(this);
            switch (((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 24, this._ctx)) {
                case 1: {
                    _localctx = new FunctionDeclarationContext(_localctx);
                    this.enterOuterAlt(_localctx, 1);
                    this.setState(288);
                    this.functionPrototype();
                    this.setState(289);
                    this.match(243);
                    return _localctx;
                }
                case 2: {
                    _localctx = new InterfaceBlockDeclarationContext(_localctx);
                    this.enterOuterAlt(_localctx, 2);
                    this.setState(291);
                    this.typeQualifier();
                    this.setState(292);
                    ((InterfaceBlockDeclarationContext)_localctx).blockName = this.match(266);
                    this.setState(293);
                    this.structBody();
                    this.setState(298);
                    this._errHandler.sync(this);
                    int _la = this._input.LA(1);
                    if (_la == 266) {
                        this.setState(294);
                        ((InterfaceBlockDeclarationContext)_localctx).variableName = this.match(266);
                        this.setState(296);
                        this._errHandler.sync(this);
                        _la = this._input.LA(1);
                        if (_la == 244) {
                            this.setState(295);
                            this.arraySpecifier();
                        }
                    }
                    this.setState(300);
                    this.match(243);
                    return _localctx;
                }
                case 3: {
                    _localctx = new VariableDeclarationContext(_localctx);
                    this.enterOuterAlt(_localctx, 3);
                    this.setState(302);
                    this.typeQualifier();
                    this.setState(311);
                    this._errHandler.sync(this);
                    int _la = this._input.LA(1);
                    if (_la == 266) {
                        this.setState(303);
                        ((VariableDeclarationContext)_localctx).IDENTIFIER = this.match(266);
                        ((VariableDeclarationContext)_localctx).variableNames.add(((VariableDeclarationContext)_localctx).IDENTIFIER);
                        this.setState(308);
                        this._errHandler.sync(this);
                        _la = this._input.LA(1);
                        while (_la == 246) {
                            this.setState(304);
                            this.match(246);
                            this.setState(305);
                            ((VariableDeclarationContext)_localctx).IDENTIFIER = this.match(266);
                            ((VariableDeclarationContext)_localctx).variableNames.add(((VariableDeclarationContext)_localctx).IDENTIFIER);
                            this.setState(310);
                            this._errHandler.sync(this);
                            _la = this._input.LA(1);
                        }
                    }
                    this.setState(313);
                    this.match(243);
                    return _localctx;
                }
                case 4: {
                    _localctx = new PrecisionDeclarationContext(_localctx);
                    this.enterOuterAlt(_localctx, 4);
                    this.setState(315);
                    this.match(10);
                    this.setState(316);
                    this.precisionQualifier();
                    this.setState(317);
                    this.typeSpecifier();
                    this.setState(318);
                    this.match(243);
                    return _localctx;
                }
                case 5: {
                    _localctx = new TypeAndInitDeclarationContext(_localctx);
                    this.enterOuterAlt(_localctx, 5);
                    this.setState(320);
                    this.fullySpecifiedType();
                    this.setState(329);
                    this._errHandler.sync(this);
                    int _la = this._input.LA(1);
                    if (_la == 266) {
                        this.setState(321);
                        ((TypeAndInitDeclarationContext)_localctx).declarationMember = this.declarationMember();
                        ((TypeAndInitDeclarationContext)_localctx).declarationMembers.add(((TypeAndInitDeclarationContext)_localctx).declarationMember);
                        this.setState(326);
                        this._errHandler.sync(this);
                        _la = this._input.LA(1);
                        while (_la == 246) {
                            this.setState(322);
                            this.match(246);
                            this.setState(323);
                            ((TypeAndInitDeclarationContext)_localctx).declarationMember = this.declarationMember();
                            ((TypeAndInitDeclarationContext)_localctx).declarationMembers.add(((TypeAndInitDeclarationContext)_localctx).declarationMember);
                            this.setState(328);
                            this._errHandler.sync(this);
                            _la = this._input.LA(1);
                        }
                    }
                    this.setState(331);
                    this.match(243);
                    return _localctx;
                }
            }
            return _localctx;
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
            return _localctx;
        }
        finally {
            this.exitRule();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final FunctionPrototypeContext functionPrototype() throws RecognitionException {
        FunctionPrototypeContext _localctx = new FunctionPrototypeContext(this._ctx, this.getState());
        this.enterRule(_localctx, 26, 13);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(336);
            this._errHandler.sync(this);
            int _la = this._input.LA(1);
            if (_la == 244) {
                this.setState(335);
                this.attribute();
            }
            this.setState(338);
            this.fullySpecifiedType();
            this.setState(339);
            this.match(266);
            this.setState(340);
            this.match(239);
            this.setState(341);
            this.functionParameterList();
            this.setState(342);
            this.match(240);
            this.setState(344);
            this._errHandler.sync(this);
            _la = this._input.LA(1);
            if (_la == 244) {
                this.setState(343);
                this.attribute();
            }
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final FunctionParameterListContext functionParameterList() throws RecognitionException {
        FunctionParameterListContext _localctx = new FunctionParameterListContext(this._ctx, this.getState());
        this.enterRule(_localctx, 28, 14);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(354);
            this._errHandler.sync(this);
            int _la = this._input.LA(1);
            if ((_la & 0xFFFFFFC0) == 0 && (1L << _la & 0x39FFFFBFFBFCL) != 0L || (_la - 69 & 0xFFFFFFC0) == 0 && (1L << _la - 69 & 0xFFFFFFFFFFFFFFFFL) != 0L || (_la - 133 & 0xFFFFFFC0) == 0 && (1L << _la - 133 & 0xFFFFFFFFFFFFFFFFL) != 0L || (_la - 197 & 0xFFFFFFC0) == 0 && (1L << _la - 197 & 0x4FFFFFL) != 0L || _la == 266) {
                this.setState(346);
                _localctx.parameterDeclaration = this.parameterDeclaration();
                _localctx.parameters.add(_localctx.parameterDeclaration);
                this.setState(351);
                this._errHandler.sync(this);
                _la = this._input.LA(1);
                while (_la == 246) {
                    this.setState(347);
                    this.match(246);
                    this.setState(348);
                    _localctx.parameterDeclaration = this.parameterDeclaration();
                    _localctx.parameters.add(_localctx.parameterDeclaration);
                    this.setState(353);
                    this._errHandler.sync(this);
                    _la = this._input.LA(1);
                }
            }
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final ParameterDeclarationContext parameterDeclaration() throws RecognitionException {
        ParameterDeclarationContext _localctx = new ParameterDeclarationContext(this._ctx, this.getState());
        this.enterRule(_localctx, 30, 15);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(356);
            this.fullySpecifiedType();
            this.setState(361);
            this._errHandler.sync(this);
            int _la = this._input.LA(1);
            if (_la == 266) {
                this.setState(357);
                _localctx.parameterName = this.match(266);
                this.setState(359);
                this._errHandler.sync(this);
                _la = this._input.LA(1);
                if (_la == 244) {
                    this.setState(358);
                    this.arraySpecifier();
                }
            }
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final AttributeContext attribute() throws RecognitionException {
        AttributeContext _localctx = new AttributeContext(this._ctx, this.getState());
        this.enterRule(_localctx, 32, 16);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(363);
            this.match(244);
            this.setState(364);
            this.match(244);
            this.setState(365);
            _localctx.singleAttribute = this.singleAttribute();
            _localctx.attributes.add(_localctx.singleAttribute);
            this.setState(370);
            this._errHandler.sync(this);
            int _la = this._input.LA(1);
            while (_la == 246) {
                this.setState(366);
                this.match(246);
                this.setState(367);
                _localctx.singleAttribute = this.singleAttribute();
                _localctx.attributes.add(_localctx.singleAttribute);
                this.setState(372);
                this._errHandler.sync(this);
                _la = this._input.LA(1);
            }
            this.setState(373);
            this.match(245);
            this.setState(374);
            this.match(245);
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final SingleAttributeContext singleAttribute() throws RecognitionException {
        SingleAttributeContext _localctx = new SingleAttributeContext(this._ctx, this.getState());
        this.enterRule(_localctx, 34, 17);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(379);
            this._errHandler.sync(this);
            switch (((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 32, this._ctx)) {
                case 1: {
                    this.setState(376);
                    _localctx.prefix = this.match(266);
                    this.setState(377);
                    this.match(1);
                    this.setState(378);
                    this.match(1);
                }
            }
            this.setState(381);
            _localctx.name = this.match(266);
            this.setState(386);
            this._errHandler.sync(this);
            int _la = this._input.LA(1);
            if (_la == 239) {
                this.setState(382);
                this.match(239);
                this.setState(383);
                _localctx.content = this.expression();
                this.setState(384);
                this.match(240);
            }
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final DeclarationMemberContext declarationMember() throws RecognitionException {
        DeclarationMemberContext _localctx = new DeclarationMemberContext(this._ctx, this.getState());
        this.enterRule(_localctx, 36, 18);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(388);
            this.match(266);
            this.setState(390);
            this._errHandler.sync(this);
            int _la = this._input.LA(1);
            if (_la == 244) {
                this.setState(389);
                this.arraySpecifier();
            }
            this.setState(394);
            this._errHandler.sync(this);
            _la = this._input.LA(1);
            if (_la == 261) {
                this.setState(392);
                this.match(261);
                this.setState(393);
                this.initializer();
            }
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final FullySpecifiedTypeContext fullySpecifiedType() throws RecognitionException {
        FullySpecifiedTypeContext _localctx = new FullySpecifiedTypeContext(this._ctx, this.getState());
        this.enterRule(_localctx, 38, 19);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(397);
            this._errHandler.sync(this);
            int _la = this._input.LA(1);
            if ((_la & 0xFFFFFFC0) == 0 && (1L << _la & 0x1FFFFBFFBFCL) != 0L) {
                this.setState(396);
                this.typeQualifier();
            }
            this.setState(399);
            this.typeSpecifier();
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public final StorageQualifierContext storageQualifier() throws RecognitionException {
        StorageQualifierContext _localctx = new StorageQualifierContext(this._ctx, this.getState());
        this.enterRule(_localctx, 40, 20);
        try {
            this.setState(441);
            this._errHandler.sync(this);
            switch (this._input.LA(1)) {
                case 11: {
                    this.enterOuterAlt(_localctx, 1);
                    this.setState(401);
                    this.match(11);
                    return _localctx;
                }
                case 4: {
                    this.enterOuterAlt(_localctx, 2);
                    this.setState(402);
                    this.match(4);
                    return _localctx;
                }
                case 5: {
                    this.enterOuterAlt(_localctx, 3);
                    this.setState(403);
                    this.match(5);
                    return _localctx;
                }
                case 6: {
                    this.enterOuterAlt(_localctx, 4);
                    this.setState(404);
                    this.match(6);
                    return _localctx;
                }
                case 16: {
                    this.enterOuterAlt(_localctx, 5);
                    this.setState(405);
                    this.match(16);
                    return _localctx;
                }
                case 25: {
                    this.enterOuterAlt(_localctx, 6);
                    this.setState(406);
                    this.match(25);
                    return _localctx;
                }
                case 24: {
                    this.enterOuterAlt(_localctx, 7);
                    this.setState(407);
                    this.match(24);
                    return _localctx;
                }
                case 2: {
                    this.enterOuterAlt(_localctx, 8);
                    this.setState(408);
                    this.match(2);
                    return _localctx;
                }
                case 19: {
                    this.enterOuterAlt(_localctx, 9);
                    this.setState(409);
                    this.match(19);
                    return _localctx;
                }
                case 17: {
                    this.enterOuterAlt(_localctx, 10);
                    this.setState(410);
                    this.match(17);
                    return _localctx;
                }
                case 3: {
                    this.enterOuterAlt(_localctx, 11);
                    this.setState(411);
                    this.match(3);
                    return _localctx;
                }
                case 20: {
                    this.enterOuterAlt(_localctx, 12);
                    this.setState(412);
                    this.match(20);
                    return _localctx;
                }
                case 27: {
                    this.enterOuterAlt(_localctx, 13);
                    this.setState(413);
                    this.match(27);
                    return _localctx;
                }
                case 18: {
                    this.enterOuterAlt(_localctx, 14);
                    this.setState(414);
                    this.match(18);
                    return _localctx;
                }
                case 26: {
                    this.enterOuterAlt(_localctx, 15);
                    this.setState(415);
                    this.match(26);
                    return _localctx;
                }
                case 28: {
                    this.enterOuterAlt(_localctx, 16);
                    this.setState(416);
                    this.match(28);
                    return _localctx;
                }
                case 29: {
                    this.enterOuterAlt(_localctx, 17);
                    this.setState(417);
                    this.match(29);
                    return _localctx;
                }
                case 30: {
                    this.enterOuterAlt(_localctx, 18);
                    this.setState(418);
                    this.match(30);
                    this.setState(429);
                    this._errHandler.sync(this);
                    int _la = this._input.LA(1);
                    if (_la != 239) return _localctx;
                    this.setState(419);
                    this.match(239);
                    this.setState(420);
                    _localctx.IDENTIFIER = this.match(266);
                    _localctx.typeNames.add(_localctx.IDENTIFIER);
                    this.setState(425);
                    this._errHandler.sync(this);
                    _la = this._input.LA(1);
                    while (_la == 246) {
                        this.setState(421);
                        this.match(246);
                        this.setState(422);
                        _localctx.IDENTIFIER = this.match(266);
                        _localctx.typeNames.add(_localctx.IDENTIFIER);
                        this.setState(427);
                        this._errHandler.sync(this);
                        _la = this._input.LA(1);
                    }
                    this.setState(428);
                    this.match(240);
                    return _localctx;
                }
                case 31: {
                    this.enterOuterAlt(_localctx, 19);
                    this.setState(431);
                    this.match(31);
                    return _localctx;
                }
                case 32: {
                    this.enterOuterAlt(_localctx, 20);
                    this.setState(432);
                    this.match(32);
                    return _localctx;
                }
                case 33: {
                    this.enterOuterAlt(_localctx, 21);
                    this.setState(433);
                    this.match(33);
                    return _localctx;
                }
                case 34: {
                    this.enterOuterAlt(_localctx, 22);
                    this.setState(434);
                    this.match(34);
                    return _localctx;
                }
                case 35: {
                    this.enterOuterAlt(_localctx, 23);
                    this.setState(435);
                    this.match(35);
                    return _localctx;
                }
                case 36: {
                    this.enterOuterAlt(_localctx, 24);
                    this.setState(436);
                    this.match(36);
                    return _localctx;
                }
                case 37: {
                    this.enterOuterAlt(_localctx, 25);
                    this.setState(437);
                    this.match(37);
                    return _localctx;
                }
                case 38: {
                    this.enterOuterAlt(_localctx, 26);
                    this.setState(438);
                    this.match(38);
                    return _localctx;
                }
                case 39: {
                    this.enterOuterAlt(_localctx, 27);
                    this.setState(439);
                    this.match(39);
                    return _localctx;
                }
                case 40: {
                    this.enterOuterAlt(_localctx, 28);
                    this.setState(440);
                    this.match(40);
                    return _localctx;
                }
                default: {
                    throw new NoViableAltException(this);
                }
            }
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
            return _localctx;
        }
        finally {
            this.exitRule();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final LayoutQualifierContext layoutQualifier() throws RecognitionException {
        LayoutQualifierContext _localctx = new LayoutQualifierContext(this._ctx, this.getState());
        this.enterRule(_localctx, 42, 21);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(443);
            this.match(21);
            this.setState(444);
            this.match(239);
            this.setState(445);
            _localctx.layoutQualifierId = this.layoutQualifierId();
            _localctx.layoutQualifiers.add(_localctx.layoutQualifierId);
            this.setState(450);
            this._errHandler.sync(this);
            int _la = this._input.LA(1);
            while (_la == 246) {
                this.setState(446);
                this.match(246);
                this.setState(447);
                _localctx.layoutQualifierId = this.layoutQualifierId();
                _localctx.layoutQualifiers.add(_localctx.layoutQualifierId);
                this.setState(452);
                this._errHandler.sync(this);
                _la = this._input.LA(1);
            }
            this.setState(453);
            this.match(240);
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public final LayoutQualifierIdContext layoutQualifierId() throws RecognitionException {
        LayoutQualifierIdContext _localctx = new LayoutQualifierIdContext(this._ctx, this.getState());
        this.enterRule(_localctx, 44, 22);
        try {
            this.setState(461);
            this._errHandler.sync(this);
            switch (this._input.LA(1)) {
                case 266: {
                    _localctx = new NamedLayoutQualifierContext(_localctx);
                    this.enterOuterAlt(_localctx, 1);
                    this.setState(455);
                    this.match(266);
                    this.setState(458);
                    this._errHandler.sync(this);
                    int _la = this._input.LA(1);
                    if (_la != 261) return _localctx;
                    this.setState(456);
                    this.match(261);
                    this.setState(457);
                    this.expression();
                    return _localctx;
                }
                case 20: {
                    _localctx = new SharedLayoutQualifierContext(_localctx);
                    this.enterOuterAlt(_localctx, 2);
                    this.setState(460);
                    this.match(20);
                    return _localctx;
                }
                default: {
                    throw new NoViableAltException(this);
                }
            }
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
            return _localctx;
        }
        finally {
            this.exitRule();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final PrecisionQualifierContext precisionQualifier() throws RecognitionException {
        PrecisionQualifierContext _localctx = new PrecisionQualifierContext(this._ctx, this.getState());
        this.enterRule(_localctx, 46, 23);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(463);
            int _la = this._input.LA(1);
            if ((_la & 0xFFFFFFC0) != 0 || (1L << _la & 0x380L) == 0L) {
                this._errHandler.recoverInline(this);
            } else {
                if (this._input.LA(1) == -1) {
                    this.matchedEOF = true;
                }
                this._errHandler.reportMatch(this);
                this.consume();
            }
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final InterpolationQualifierContext interpolationQualifier() throws RecognitionException {
        InterpolationQualifierContext _localctx = new InterpolationQualifierContext(this._ctx, this.getState());
        this.enterRule(_localctx, 48, 24);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(465);
            int _la = this._input.LA(1);
            if ((_la & 0xFFFFFFC0) != 0 || (1L << _la & 0x80C000L) == 0L) {
                this._errHandler.recoverInline(this);
            } else {
                if (this._input.LA(1) == -1) {
                    this.matchedEOF = true;
                }
                this._errHandler.reportMatch(this);
                this.consume();
            }
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    public final InvariantQualifierContext invariantQualifier() throws RecognitionException {
        InvariantQualifierContext _localctx = new InvariantQualifierContext(this._ctx, this.getState());
        this.enterRule(_localctx, 50, 25);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(467);
            this.match(13);
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    public final PreciseQualifierContext preciseQualifier() throws RecognitionException {
        PreciseQualifierContext _localctx = new PreciseQualifierContext(this._ctx, this.getState());
        this.enterRule(_localctx, 52, 26);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(469);
            this.match(12);
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final TypeQualifierContext typeQualifier() throws RecognitionException {
        TypeQualifierContext _localctx = new TypeQualifierContext(this._ctx, this.getState());
        this.enterRule(_localctx, 54, 27);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(477);
            this._errHandler.sync(this);
            int _la = this._input.LA(1);
            do {
                this.setState(477);
                this._errHandler.sync(this);
                switch (this._input.LA(1)) {
                    case 2: 
                    case 3: 
                    case 4: 
                    case 5: 
                    case 6: 
                    case 11: 
                    case 16: 
                    case 17: 
                    case 18: 
                    case 19: 
                    case 20: 
                    case 24: 
                    case 25: 
                    case 26: 
                    case 27: 
                    case 28: 
                    case 29: 
                    case 30: 
                    case 31: 
                    case 32: 
                    case 33: 
                    case 34: 
                    case 35: 
                    case 36: 
                    case 37: 
                    case 38: 
                    case 39: 
                    case 40: {
                        this.setState(471);
                        this.storageQualifier();
                        break;
                    }
                    case 21: {
                        this.setState(472);
                        this.layoutQualifier();
                        break;
                    }
                    case 7: 
                    case 8: 
                    case 9: {
                        this.setState(473);
                        this.precisionQualifier();
                        break;
                    }
                    case 14: 
                    case 15: 
                    case 23: {
                        this.setState(474);
                        this.interpolationQualifier();
                        break;
                    }
                    case 13: {
                        this.setState(475);
                        this.invariantQualifier();
                        break;
                    }
                    case 12: {
                        this.setState(476);
                        this.preciseQualifier();
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this);
                    }
                }
                this.setState(479);
                this._errHandler.sync(this);
            } while (((_la = this._input.LA(1)) & 0xFFFFFFC0) == 0 && (1L << _la & 0x1FFFFBFFBFCL) != 0L);
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final TypeSpecifierContext typeSpecifier() throws RecognitionException {
        TypeSpecifierContext _localctx = new TypeSpecifierContext(this._ctx, this.getState());
        this.enterRule(_localctx, 56, 28);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(485);
            this._errHandler.sync(this);
            switch (this._input.LA(1)) {
                case 266: {
                    this.setState(481);
                    this.match(266);
                    break;
                }
                case 43: 
                case 44: 
                case 144: 
                case 145: 
                case 146: 
                case 147: 
                case 148: 
                case 149: 
                case 150: 
                case 151: 
                case 152: 
                case 153: 
                case 154: 
                case 155: 
                case 156: 
                case 157: 
                case 158: 
                case 159: 
                case 160: 
                case 161: 
                case 162: 
                case 163: 
                case 164: 
                case 165: 
                case 166: 
                case 167: 
                case 168: 
                case 169: 
                case 170: 
                case 171: 
                case 172: 
                case 173: 
                case 174: 
                case 175: 
                case 176: 
                case 177: 
                case 178: 
                case 179: 
                case 180: 
                case 181: 
                case 182: 
                case 183: 
                case 184: 
                case 185: 
                case 186: 
                case 187: 
                case 188: 
                case 189: 
                case 190: 
                case 191: 
                case 192: 
                case 193: 
                case 194: 
                case 195: 
                case 196: 
                case 197: 
                case 198: 
                case 199: 
                case 200: 
                case 201: 
                case 202: 
                case 203: 
                case 204: 
                case 205: 
                case 206: 
                case 207: 
                case 208: 
                case 209: 
                case 210: 
                case 211: 
                case 212: 
                case 213: 
                case 214: 
                case 215: 
                case 216: 
                case 219: {
                    this.setState(482);
                    this.builtinTypeSpecifierFixed();
                    break;
                }
                case 69: 
                case 70: 
                case 71: 
                case 72: 
                case 73: 
                case 74: 
                case 75: 
                case 76: 
                case 77: 
                case 78: 
                case 79: 
                case 80: 
                case 81: 
                case 82: 
                case 83: 
                case 84: 
                case 85: 
                case 86: 
                case 87: 
                case 88: 
                case 89: 
                case 90: 
                case 91: 
                case 92: 
                case 93: 
                case 94: 
                case 95: 
                case 96: 
                case 97: 
                case 98: 
                case 99: 
                case 100: 
                case 101: 
                case 102: 
                case 103: 
                case 104: 
                case 105: 
                case 106: 
                case 107: 
                case 108: 
                case 109: 
                case 110: 
                case 111: 
                case 112: 
                case 113: 
                case 114: 
                case 115: 
                case 116: 
                case 117: 
                case 118: 
                case 119: 
                case 120: 
                case 121: 
                case 122: 
                case 123: 
                case 124: 
                case 125: 
                case 126: 
                case 127: 
                case 128: 
                case 129: 
                case 130: 
                case 131: 
                case 132: 
                case 133: 
                case 134: 
                case 135: 
                case 136: 
                case 137: 
                case 138: 
                case 139: 
                case 140: 
                case 141: 
                case 142: 
                case 143: {
                    this.setState(483);
                    this.builtinTypeSpecifierParseable();
                    break;
                }
                case 45: {
                    this.setState(484);
                    this.structSpecifier();
                    break;
                }
                default: {
                    throw new NoViableAltException(this);
                }
            }
            this.setState(488);
            this._errHandler.sync(this);
            int _la = this._input.LA(1);
            if (_la == 244) {
                this.setState(487);
                this.arraySpecifier();
            }
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final ArraySpecifierContext arraySpecifier() throws RecognitionException {
        ArraySpecifierContext _localctx = new ArraySpecifierContext(this._ctx, this.getState());
        this.enterRule(_localctx, 58, 29);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(491);
            this._errHandler.sync(this);
            int _la = this._input.LA(1);
            do {
                this.setState(490);
                this.arraySpecifierSegment();
                this.setState(493);
                this._errHandler.sync(this);
            } while ((_la = this._input.LA(1)) == 244);
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final ArraySpecifierSegmentContext arraySpecifierSegment() throws RecognitionException {
        ArraySpecifierSegmentContext _localctx = new ArraySpecifierSegmentContext(this._ctx, this.getState());
        this.enterRule(_localctx, 60, 30);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(495);
            this.match(244);
            this.setState(497);
            this._errHandler.sync(this);
            int _la = this._input.LA(1);
            if ((_la - 43 & 0xFFFFFFC0) == 0 && (1L << _la - 43 & 0xFFFFFFFFFFFF0007L) != 0L || (_la - 107 & 0xFFFFFFC0) == 0 && (1L << _la - 107 & 0xFFFFFFFFFFFFFFFFL) != 0L || (_la - 171 & 0xFFFFFFC0) == 0 && (1L << _la - 171 & 0x1FFFFFFFFFFFFL) != 0L || (_la - 239 & 0xFFFFFFC0) == 0 && (1L << _la - 239 & 0x8001E01L) != 0L) {
                this.setState(496);
                this.expression();
            }
            this.setState(499);
            this.match(245);
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final BuiltinTypeSpecifierParseableContext builtinTypeSpecifierParseable() throws RecognitionException {
        BuiltinTypeSpecifierParseableContext _localctx = new BuiltinTypeSpecifierParseableContext(this._ctx, this.getState());
        this.enterRule(_localctx, 62, 31);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(501);
            int _la = this._input.LA(1);
            if (!((_la - 69 & 0xFFFFFFC0) == 0 && (1L << _la - 69 & 0xFFFFFFFFFFFFFFFFL) != 0L || (_la - 133 & 0xFFFFFFC0) == 0 && (1L << _la - 133 & 0x7FFL) != 0L)) {
                this._errHandler.recoverInline(this);
            } else {
                if (this._input.LA(1) == -1) {
                    this.matchedEOF = true;
                }
                this._errHandler.reportMatch(this);
                this.consume();
            }
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final BuiltinTypeSpecifierFixedContext builtinTypeSpecifierFixed() throws RecognitionException {
        BuiltinTypeSpecifierFixedContext _localctx = new BuiltinTypeSpecifierFixedContext(this._ctx, this.getState());
        this.enterRule(_localctx, 64, 32);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(503);
            int _la = this._input.LA(1);
            if (!(_la == 43 || _la == 44 || (_la - 144 & 0xFFFFFFC0) == 0 && (1L << _la - 144 & 0xFFFFFFFFFFFFFFFFL) != 0L || (_la - 208 & 0xFFFFFFC0) == 0 && (1L << _la - 208 & 0x9FFL) != 0L)) {
                this._errHandler.recoverInline(this);
            } else {
                if (this._input.LA(1) == -1) {
                    this.matchedEOF = true;
                }
                this._errHandler.reportMatch(this);
                this.consume();
            }
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final StructSpecifierContext structSpecifier() throws RecognitionException {
        StructSpecifierContext _localctx = new StructSpecifierContext(this._ctx, this.getState());
        this.enterRule(_localctx, 66, 33);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(505);
            this.match(45);
            this.setState(507);
            this._errHandler.sync(this);
            int _la = this._input.LA(1);
            if (_la == 266) {
                this.setState(506);
                this.match(266);
            }
            this.setState(509);
            this.structBody();
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final StructBodyContext structBody() throws RecognitionException {
        StructBodyContext _localctx = new StructBodyContext(this._ctx, this.getState());
        this.enterRule(_localctx, 68, 34);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(511);
            this.match(241);
            this.setState(513);
            this._errHandler.sync(this);
            int _la = this._input.LA(1);
            do {
                this.setState(512);
                this.structMember();
                this.setState(515);
                this._errHandler.sync(this);
            } while (((_la = this._input.LA(1)) & 0xFFFFFFC0) == 0 && (1L << _la & 0x39FFFFBFFBFCL) != 0L || (_la - 69 & 0xFFFFFFC0) == 0 && (1L << _la - 69 & 0xFFFFFFFFFFFFFFFFL) != 0L || (_la - 133 & 0xFFFFFFC0) == 0 && (1L << _la - 133 & 0xFFFFFFFFFFFFFFFFL) != 0L || (_la - 197 & 0xFFFFFFC0) == 0 && (1L << _la - 197 & 0x4FFFFFL) != 0L || _la == 266);
            this.setState(517);
            this.match(242);
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final StructMemberContext structMember() throws RecognitionException {
        StructMemberContext _localctx = new StructMemberContext(this._ctx, this.getState());
        this.enterRule(_localctx, 70, 35);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(519);
            this.fullySpecifiedType();
            this.setState(520);
            _localctx.structDeclarator = this.structDeclarator();
            _localctx.structDeclarators.add(_localctx.structDeclarator);
            this.setState(525);
            this._errHandler.sync(this);
            int _la = this._input.LA(1);
            while (_la == 246) {
                this.setState(521);
                this.match(246);
                this.setState(522);
                _localctx.structDeclarator = this.structDeclarator();
                _localctx.structDeclarators.add(_localctx.structDeclarator);
                this.setState(527);
                this._errHandler.sync(this);
                _la = this._input.LA(1);
            }
            this.setState(528);
            this.match(243);
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final StructDeclaratorContext structDeclarator() throws RecognitionException {
        StructDeclaratorContext _localctx = new StructDeclaratorContext(this._ctx, this.getState());
        this.enterRule(_localctx, 72, 36);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(530);
            this.match(266);
            this.setState(532);
            this._errHandler.sync(this);
            int _la = this._input.LA(1);
            if (_la == 244) {
                this.setState(531);
                this.arraySpecifier();
            }
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public final InitializerContext initializer() throws RecognitionException {
        InitializerContext _localctx = new InitializerContext(this._ctx, this.getState());
        this.enterRule(_localctx, 74, 37);
        try {
            this.setState(550);
            this._errHandler.sync(this);
            switch (this._input.LA(1)) {
                case 43: 
                case 44: 
                case 45: 
                case 59: 
                case 60: 
                case 61: 
                case 62: 
                case 63: 
                case 64: 
                case 65: 
                case 66: 
                case 67: 
                case 68: 
                case 69: 
                case 70: 
                case 71: 
                case 72: 
                case 73: 
                case 74: 
                case 75: 
                case 76: 
                case 77: 
                case 78: 
                case 79: 
                case 80: 
                case 81: 
                case 82: 
                case 83: 
                case 84: 
                case 85: 
                case 86: 
                case 87: 
                case 88: 
                case 89: 
                case 90: 
                case 91: 
                case 92: 
                case 93: 
                case 94: 
                case 95: 
                case 96: 
                case 97: 
                case 98: 
                case 99: 
                case 100: 
                case 101: 
                case 102: 
                case 103: 
                case 104: 
                case 105: 
                case 106: 
                case 107: 
                case 108: 
                case 109: 
                case 110: 
                case 111: 
                case 112: 
                case 113: 
                case 114: 
                case 115: 
                case 116: 
                case 117: 
                case 118: 
                case 119: 
                case 120: 
                case 121: 
                case 122: 
                case 123: 
                case 124: 
                case 125: 
                case 126: 
                case 127: 
                case 128: 
                case 129: 
                case 130: 
                case 131: 
                case 132: 
                case 133: 
                case 134: 
                case 135: 
                case 136: 
                case 137: 
                case 138: 
                case 139: 
                case 140: 
                case 141: 
                case 142: 
                case 143: 
                case 144: 
                case 145: 
                case 146: 
                case 147: 
                case 148: 
                case 149: 
                case 150: 
                case 151: 
                case 152: 
                case 153: 
                case 154: 
                case 155: 
                case 156: 
                case 157: 
                case 158: 
                case 159: 
                case 160: 
                case 161: 
                case 162: 
                case 163: 
                case 164: 
                case 165: 
                case 166: 
                case 167: 
                case 168: 
                case 169: 
                case 170: 
                case 171: 
                case 172: 
                case 173: 
                case 174: 
                case 175: 
                case 176: 
                case 177: 
                case 178: 
                case 179: 
                case 180: 
                case 181: 
                case 182: 
                case 183: 
                case 184: 
                case 185: 
                case 186: 
                case 187: 
                case 188: 
                case 189: 
                case 190: 
                case 191: 
                case 192: 
                case 193: 
                case 194: 
                case 195: 
                case 196: 
                case 197: 
                case 198: 
                case 199: 
                case 200: 
                case 201: 
                case 202: 
                case 203: 
                case 204: 
                case 205: 
                case 206: 
                case 207: 
                case 208: 
                case 209: 
                case 210: 
                case 211: 
                case 212: 
                case 213: 
                case 214: 
                case 215: 
                case 216: 
                case 217: 
                case 218: 
                case 219: 
                case 239: 
                case 248: 
                case 249: 
                case 250: 
                case 251: 
                case 266: {
                    this.enterOuterAlt(_localctx, 1);
                    this.setState(534);
                    this.finiteExpression(0);
                    return _localctx;
                }
                case 241: {
                    this.enterOuterAlt(_localctx, 2);
                    this.setState(535);
                    this.match(241);
                    this.setState(547);
                    this._errHandler.sync(this);
                    int _la = this._input.LA(1);
                    if ((_la - 43 & 0xFFFFFFC0) == 0 && (1L << _la - 43 & 0xFFFFFFFFFFFF0007L) != 0L || (_la - 107 & 0xFFFFFFC0) == 0 && (1L << _la - 107 & 0xFFFFFFFFFFFFFFFFL) != 0L || (_la - 171 & 0xFFFFFFC0) == 0 && (1L << _la - 171 & 0x1FFFFFFFFFFFFL) != 0L || (_la - 239 & 0xFFFFFFC0) == 0 && (1L << _la - 239 & 0x8001E05L) != 0L) {
                        this.setState(536);
                        _localctx.initializer = this.initializer();
                        _localctx.initializers.add(_localctx.initializer);
                        this.setState(541);
                        this._errHandler.sync(this);
                        int _alt = ((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 53, this._ctx);
                        while (_alt != 2 && _alt != 0) {
                            if (_alt == 1) {
                                this.setState(537);
                                this.match(246);
                                this.setState(538);
                                _localctx.initializer = this.initializer();
                                _localctx.initializers.add(_localctx.initializer);
                            }
                            this.setState(543);
                            this._errHandler.sync(this);
                            _alt = ((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 53, this._ctx);
                        }
                        this.setState(545);
                        this._errHandler.sync(this);
                        _la = this._input.LA(1);
                        if (_la == 246) {
                            this.setState(544);
                            this.match(246);
                        }
                    }
                    this.setState(549);
                    this.match(242);
                    return _localctx;
                }
                default: {
                    throw new NoViableAltException(this);
                }
            }
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
            return _localctx;
        }
        finally {
            this.exitRule();
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public final StatementContext statement() throws RecognitionException {
        StatementContext _localctx = new StatementContext(this._ctx, this.getState());
        this.enterRule(_localctx, 76, 38);
        try {
            this.setState(564);
            this._errHandler.sync(this);
            switch (((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 57, this._ctx)) {
                case 1: {
                    this.enterOuterAlt(_localctx, 1);
                    this.setState(552);
                    this.compoundStatement();
                    return _localctx;
                }
                case 2: {
                    this.enterOuterAlt(_localctx, 2);
                    this.setState(553);
                    this.declarationStatement();
                    return _localctx;
                }
                case 3: {
                    this.enterOuterAlt(_localctx, 3);
                    this.setState(554);
                    this.expressionStatement();
                    return _localctx;
                }
                case 4: {
                    this.enterOuterAlt(_localctx, 4);
                    this.setState(555);
                    this.emptyStatement();
                    return _localctx;
                }
                case 5: {
                    this.enterOuterAlt(_localctx, 5);
                    this.setState(556);
                    this.selectionStatement();
                    return _localctx;
                }
                case 6: {
                    this.enterOuterAlt(_localctx, 6);
                    this.setState(557);
                    this.switchStatement();
                    return _localctx;
                }
                case 7: {
                    this.enterOuterAlt(_localctx, 7);
                    this.setState(558);
                    this.caseLabel();
                    return _localctx;
                }
                case 8: {
                    this.enterOuterAlt(_localctx, 8);
                    this.setState(559);
                    this.forStatement();
                    return _localctx;
                }
                case 9: {
                    this.enterOuterAlt(_localctx, 9);
                    this.setState(560);
                    this.whileStatement();
                    return _localctx;
                }
                case 10: {
                    this.enterOuterAlt(_localctx, 10);
                    this.setState(561);
                    this.doWhileStatement();
                    return _localctx;
                }
                case 11: {
                    this.enterOuterAlt(_localctx, 11);
                    this.setState(562);
                    this.jumpStatement();
                    return _localctx;
                }
                case 12: {
                    this.enterOuterAlt(_localctx, 12);
                    this.setState(563);
                    this.demoteStatement();
                    return _localctx;
                }
            }
            return _localctx;
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
            return _localctx;
        }
        finally {
            this.exitRule();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final CompoundStatementContext compoundStatement() throws RecognitionException {
        CompoundStatementContext _localctx = new CompoundStatementContext(this._ctx, this.getState());
        this.enterRule(_localctx, 78, 39);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(566);
            this.match(241);
            this.setState(570);
            this._errHandler.sync(this);
            int _la = this._input.LA(1);
            while ((_la & 0xFFFFFFC0) == 0 && (1L << _la & 0xFFFF7FFFFFBFFFFCL) != 0L || (_la - 64 & 0xFFFFFFC0) == 0 && (1L << _la - 64 & 0xFFFFFFFFFFFFFFFFL) != 0L || (_la - 128 & 0xFFFFFFC0) == 0 && (1L << _la - 128 & 0xFFFFFFFFFFFFFFFFL) != 0L || (_la - 192 & 0xFFFFFFC0) == 0 && (1L << _la - 192 & 0xF1A80000FFFFFFFL) != 0L || _la == 266) {
                this.setState(567);
                this.statement();
                this.setState(572);
                this._errHandler.sync(this);
                _la = this._input.LA(1);
            }
            this.setState(573);
            this.match(242);
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    public final DeclarationStatementContext declarationStatement() throws RecognitionException {
        DeclarationStatementContext _localctx = new DeclarationStatementContext(this._ctx, this.getState());
        this.enterRule(_localctx, 80, 40);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(575);
            this.declaration();
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    public final ExpressionStatementContext expressionStatement() throws RecognitionException {
        ExpressionStatementContext _localctx = new ExpressionStatementContext(this._ctx, this.getState());
        this.enterRule(_localctx, 82, 41);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(577);
            this.expression();
            this.setState(578);
            this.match(243);
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    public final EmptyStatementContext emptyStatement() throws RecognitionException {
        EmptyStatementContext _localctx = new EmptyStatementContext(this._ctx, this.getState());
        this.enterRule(_localctx, 84, 42);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(580);
            this.match(243);
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public final SelectionStatementContext selectionStatement() throws RecognitionException {
        SelectionStatementContext _localctx = new SelectionStatementContext(this._ctx, this.getState());
        this.enterRule(_localctx, 86, 43);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(583);
            this._errHandler.sync(this);
            int _la = this._input.LA(1);
            if (_la == 244) {
                this.setState(582);
                this.attribute();
            }
            this.setState(585);
            this.match(46);
            this.setState(586);
            this.match(239);
            this.setState(587);
            _localctx.condition = this.expression();
            this.setState(588);
            this.match(240);
            this.setState(589);
            _localctx.ifTrue = this.statement();
            this.setState(592);
            this._errHandler.sync(this);
            switch (((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 60, this._ctx)) {
                case 1: {
                    this.setState(590);
                    this.match(47);
                    this.setState(591);
                    _localctx.ifFalse = this.statement();
                    return _localctx;
                }
            }
            return _localctx;
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
            return _localctx;
        }
        finally {
            this.exitRule();
        }
    }

    public final IterationConditionContext iterationCondition() throws RecognitionException {
        IterationConditionContext _localctx = new IterationConditionContext(this._ctx, this.getState());
        this.enterRule(_localctx, 88, 44);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(594);
            this.fullySpecifiedType();
            this.setState(595);
            _localctx.name = this.match(266);
            this.setState(596);
            this.match(261);
            this.setState(597);
            this.initializer();
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final SwitchStatementContext switchStatement() throws RecognitionException {
        SwitchStatementContext _localctx = new SwitchStatementContext(this._ctx, this.getState());
        this.enterRule(_localctx, 90, 45);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(600);
            this._errHandler.sync(this);
            int _la = this._input.LA(1);
            if (_la == 244) {
                this.setState(599);
                this.attribute();
            }
            this.setState(602);
            this.match(48);
            this.setState(603);
            this.match(239);
            this.setState(604);
            _localctx.condition = this.expression();
            this.setState(605);
            this.match(240);
            this.setState(606);
            this.compoundStatement();
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public final CaseLabelContext caseLabel() throws RecognitionException {
        CaseLabelContext _localctx = new CaseLabelContext(this._ctx, this.getState());
        this.enterRule(_localctx, 92, 46);
        try {
            this.setState(614);
            this._errHandler.sync(this);
            switch (this._input.LA(1)) {
                case 49: {
                    _localctx = new ValuedCaseLabelContext(_localctx);
                    this.enterOuterAlt(_localctx, 1);
                    this.setState(608);
                    this.match(49);
                    this.setState(609);
                    this.expression();
                    this.setState(610);
                    this.match(1);
                    return _localctx;
                }
                case 50: {
                    _localctx = new DefaultCaseLabelContext(_localctx);
                    this.enterOuterAlt(_localctx, 2);
                    this.setState(612);
                    this.match(50);
                    this.setState(613);
                    this.match(1);
                    return _localctx;
                }
                default: {
                    throw new NoViableAltException(this);
                }
            }
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
            return _localctx;
        }
        finally {
            this.exitRule();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final WhileStatementContext whileStatement() throws RecognitionException {
        WhileStatementContext _localctx = new WhileStatementContext(this._ctx, this.getState());
        this.enterRule(_localctx, 94, 47);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(617);
            this._errHandler.sync(this);
            int _la = this._input.LA(1);
            if (_la == 244) {
                this.setState(616);
                this.attribute();
            }
            this.setState(619);
            this.match(51);
            this.setState(620);
            this.match(239);
            this.setState(623);
            this._errHandler.sync(this);
            switch (((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 64, this._ctx)) {
                case 1: {
                    this.setState(621);
                    _localctx.condition = this.expression();
                    break;
                }
                case 2: {
                    this.setState(622);
                    _localctx.initCondition = this.iterationCondition();
                }
            }
            this.setState(625);
            this.match(240);
            this.setState(626);
            _localctx.loopBody = this.statement();
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final DoWhileStatementContext doWhileStatement() throws RecognitionException {
        DoWhileStatementContext _localctx = new DoWhileStatementContext(this._ctx, this.getState());
        this.enterRule(_localctx, 96, 48);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(629);
            this._errHandler.sync(this);
            int _la = this._input.LA(1);
            if (_la == 244) {
                this.setState(628);
                this.attribute();
            }
            this.setState(631);
            this.match(52);
            this.setState(632);
            _localctx.loopBody = this.statement();
            this.setState(633);
            this.match(51);
            this.setState(634);
            this.match(239);
            this.setState(635);
            _localctx.condition = this.expression();
            this.setState(636);
            this.match(240);
            this.setState(637);
            this.match(243);
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final ForStatementContext forStatement() throws RecognitionException {
        ForStatementContext _localctx = new ForStatementContext(this._ctx, this.getState());
        this.enterRule(_localctx, 98, 49);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(640);
            this._errHandler.sync(this);
            int _la = this._input.LA(1);
            if (_la == 244) {
                this.setState(639);
                this.attribute();
            }
            this.setState(642);
            this.match(53);
            this.setState(643);
            this.match(239);
            this.setState(647);
            this._errHandler.sync(this);
            switch (((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 67, this._ctx)) {
                case 1: {
                    this.setState(644);
                    this.emptyStatement();
                    break;
                }
                case 2: {
                    this.setState(645);
                    this.expressionStatement();
                    break;
                }
                case 3: {
                    this.setState(646);
                    this.declarationStatement();
                }
            }
            this.setState(651);
            this._errHandler.sync(this);
            switch (((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 68, this._ctx)) {
                case 1: {
                    this.setState(649);
                    _localctx.condition = this.expression();
                    break;
                }
                case 2: {
                    this.setState(650);
                    _localctx.initCondition = this.iterationCondition();
                }
            }
            this.setState(653);
            this.match(243);
            this.setState(655);
            this._errHandler.sync(this);
            _la = this._input.LA(1);
            if ((_la - 43 & 0xFFFFFFC0) == 0 && (1L << _la - 43 & 0xFFFFFFFFFFFF0007L) != 0L || (_la - 107 & 0xFFFFFFC0) == 0 && (1L << _la - 107 & 0xFFFFFFFFFFFFFFFFL) != 0L || (_la - 171 & 0xFFFFFFC0) == 0 && (1L << _la - 171 & 0x1FFFFFFFFFFFFL) != 0L || (_la - 239 & 0xFFFFFFC0) == 0 && (1L << _la - 239 & 0x8001E01L) != 0L) {
                this.setState(654);
                _localctx.incrementer = this.expression();
            }
            this.setState(657);
            this.match(240);
            this.setState(658);
            _localctx.loopBody = this.statement();
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public final JumpStatementContext jumpStatement() throws RecognitionException {
        JumpStatementContext _localctx = new JumpStatementContext(this._ctx, this.getState());
        this.enterRule(_localctx, 100, 50);
        try {
            this.setState(675);
            this._errHandler.sync(this);
            switch (this._input.LA(1)) {
                case 54: {
                    _localctx = new ContinueStatementContext(_localctx);
                    this.enterOuterAlt(_localctx, 1);
                    this.setState(660);
                    this.match(54);
                    this.setState(661);
                    this.match(243);
                    return _localctx;
                }
                case 55: {
                    _localctx = new BreakStatementContext(_localctx);
                    this.enterOuterAlt(_localctx, 2);
                    this.setState(662);
                    this.match(55);
                    this.setState(663);
                    this.match(243);
                    return _localctx;
                }
                case 56: {
                    _localctx = new ReturnStatementContext(_localctx);
                    this.enterOuterAlt(_localctx, 3);
                    this.setState(664);
                    this.match(56);
                    this.setState(666);
                    this._errHandler.sync(this);
                    int _la = this._input.LA(1);
                    if ((_la - 43 & 0xFFFFFFC0) == 0 && (1L << _la - 43 & 0xFFFFFFFFFFFF0007L) != 0L || (_la - 107 & 0xFFFFFFC0) == 0 && (1L << _la - 107 & 0xFFFFFFFFFFFFFFFFL) != 0L || (_la - 171 & 0xFFFFFFC0) == 0 && (1L << _la - 171 & 0x1FFFFFFFFFFFFL) != 0L || (_la - 239 & 0xFFFFFFC0) == 0 && (1L << _la - 239 & 0x8001E01L) != 0L) {
                        this.setState(665);
                        this.expression();
                    }
                    this.setState(668);
                    this.match(243);
                    return _localctx;
                }
                case 57: {
                    _localctx = new DiscardStatementContext(_localctx);
                    this.enterOuterAlt(_localctx, 4);
                    this.setState(669);
                    this.match(57);
                    this.setState(670);
                    this.match(243);
                    return _localctx;
                }
                case 41: {
                    _localctx = new IgnoreIntersectionStatementContext(_localctx);
                    this.enterOuterAlt(_localctx, 5);
                    this.setState(671);
                    this.match(41);
                    this.setState(672);
                    this.match(243);
                    return _localctx;
                }
                case 42: {
                    _localctx = new TerminateRayStatementContext(_localctx);
                    this.enterOuterAlt(_localctx, 6);
                    this.setState(673);
                    this.match(42);
                    this.setState(674);
                    this.match(243);
                    return _localctx;
                }
                default: {
                    throw new NoViableAltException(this);
                }
            }
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
            return _localctx;
        }
        finally {
            this.exitRule();
        }
    }

    public final DemoteStatementContext demoteStatement() throws RecognitionException {
        DemoteStatementContext _localctx = new DemoteStatementContext(this._ctx, this.getState());
        this.enterRule(_localctx, 102, 51);
        try {
            this.enterOuterAlt(_localctx, 1);
            this.setState(677);
            this.match(58);
            this.setState(678);
            this.match(243);
        }
        catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        }
        finally {
            this.exitRule();
        }
        return _localctx;
    }

    @Override
    public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
        switch (ruleIndex) {
            case 10: {
                return this.finiteExpression_sempred((FiniteExpressionContext)_localctx, predIndex);
            }
        }
        return true;
    }

    private boolean finiteExpression_sempred(FiniteExpressionContext _localctx, int predIndex) {
        switch (predIndex) {
            case 0: {
                return this.precpred(this._ctx, 13);
            }
            case 1: {
                return this.precpred(this._ctx, 12);
            }
            case 2: {
                return this.precpred(this._ctx, 11);
            }
            case 3: {
                return this.precpred(this._ctx, 10);
            }
            case 4: {
                return this.precpred(this._ctx, 9);
            }
            case 5: {
                return this.precpred(this._ctx, 8);
            }
            case 6: {
                return this.precpred(this._ctx, 7);
            }
            case 7: {
                return this.precpred(this._ctx, 6);
            }
            case 8: {
                return this.precpred(this._ctx, 5);
            }
            case 9: {
                return this.precpred(this._ctx, 4);
            }
            case 10: {
                return this.precpred(this._ctx, 3);
            }
            case 11: {
                return this.precpred(this._ctx, 2);
            }
            case 12: {
                return this.precpred(this._ctx, 1);
            }
            case 13: {
                return this.precpred(this._ctx, 19);
            }
            case 14: {
                return this.precpred(this._ctx, 18);
            }
            case 15: {
                return this.precpred(this._ctx, 16);
            }
            case 16: {
                return this.precpred(this._ctx, 15);
            }
        }
        return true;
    }

    static {
        int i;
        RuntimeMetaData.checkVersion("4.11.1", "4.11.1");
        _sharedContextCache = new PredictionContextCache();
        ruleNames = GLSLParser.makeRuleNames();
        _LITERAL_NAMES = GLSLParser.makeLiteralNames();
        _SYMBOLIC_NAMES = GLSLParser.makeSymbolicNames();
        VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);
        tokenNames = new String[_SYMBOLIC_NAMES.length];
        for (i = 0; i < tokenNames.length; ++i) {
            GLSLParser.tokenNames[i] = VOCABULARY.getLiteralName(i);
            if (tokenNames[i] == null) {
                GLSLParser.tokenNames[i] = VOCABULARY.getSymbolicName(i);
            }
            if (tokenNames[i] != null) continue;
            GLSLParser.tokenNames[i] = "<INVALID>";
        }
        _ATN = new ATNDeserializer().deserialize(_serializedATN.toCharArray());
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (i = 0; i < _ATN.getNumberOfDecisions(); ++i) {
            GLSLParser._decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }

    public static class TranslationUnitContext
    extends ParserRuleContext {
        public TerminalNode EOF() {
            return this.getToken(-1, 0);
        }

        public VersionStatementContext versionStatement() {
            return this.getRuleContext(VersionStatementContext.class, 0);
        }

        public List<ExternalDeclarationContext> externalDeclaration() {
            return this.getRuleContexts(ExternalDeclarationContext.class);
        }

        public ExternalDeclarationContext externalDeclaration(int i) {
            return this.getRuleContext(ExternalDeclarationContext.class, i);
        }

        public TranslationUnitContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 0;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterTranslationUnit(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitTranslationUnit(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitTranslationUnit(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class VersionStatementContext
    extends ParserRuleContext {
        public Token version;
        public Token profile;

        public TerminalNode NR() {
            return this.getToken(265, 0);
        }

        public TerminalNode NR_VERSION() {
            return this.getToken(273, 0);
        }

        public TerminalNode NR_EOL() {
            return this.getToken(318, 0);
        }

        public TerminalNode NR_GLSL_110() {
            return this.getToken(294, 0);
        }

        public TerminalNode NR_GLSL_120() {
            return this.getToken(295, 0);
        }

        public TerminalNode NR_GLSLES_100() {
            return this.getToken(296, 0);
        }

        public TerminalNode NR_GLSL_130() {
            return this.getToken(297, 0);
        }

        public TerminalNode NR_GLSL_140() {
            return this.getToken(298, 0);
        }

        public TerminalNode NR_GLSL_150() {
            return this.getToken(299, 0);
        }

        public TerminalNode NR_GLSL_330() {
            return this.getToken(300, 0);
        }

        public TerminalNode NR_GLSLES_300() {
            return this.getToken(301, 0);
        }

        public TerminalNode NR_GLSLES_310() {
            return this.getToken(302, 0);
        }

        public TerminalNode NR_GLSLES_320() {
            return this.getToken(303, 0);
        }

        public TerminalNode NR_GLSL_400() {
            return this.getToken(304, 0);
        }

        public TerminalNode NR_GLSL_410() {
            return this.getToken(305, 0);
        }

        public TerminalNode NR_GLSL_420() {
            return this.getToken(306, 0);
        }

        public TerminalNode NR_GLSL_430() {
            return this.getToken(307, 0);
        }

        public TerminalNode NR_GLSL_440() {
            return this.getToken(308, 0);
        }

        public TerminalNode NR_GLSL_450() {
            return this.getToken(309, 0);
        }

        public TerminalNode NR_GLSL_460() {
            return this.getToken(310, 0);
        }

        public TerminalNode NR_CORE() {
            return this.getToken(291, 0);
        }

        public TerminalNode NR_COMPATIBILITY() {
            return this.getToken(292, 0);
        }

        public TerminalNode NR_ES() {
            return this.getToken(293, 0);
        }

        public VersionStatementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 1;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterVersionStatement(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitVersionStatement(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitVersionStatement(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class ExternalDeclarationContext
    extends ParserRuleContext {
        public FunctionDefinitionContext functionDefinition() {
            return this.getRuleContext(FunctionDefinitionContext.class, 0);
        }

        public DeclarationContext declaration() {
            return this.getRuleContext(DeclarationContext.class, 0);
        }

        public PragmaDirectiveContext pragmaDirective() {
            return this.getRuleContext(PragmaDirectiveContext.class, 0);
        }

        public ExtensionDirectiveContext extensionDirective() {
            return this.getRuleContext(ExtensionDirectiveContext.class, 0);
        }

        public CustomDirectiveContext customDirective() {
            return this.getRuleContext(CustomDirectiveContext.class, 0);
        }

        public IncludeDirectiveContext includeDirective() {
            return this.getRuleContext(IncludeDirectiveContext.class, 0);
        }

        public LayoutDefaultsContext layoutDefaults() {
            return this.getRuleContext(LayoutDefaultsContext.class, 0);
        }

        public EmptyDeclarationContext emptyDeclaration() {
            return this.getRuleContext(EmptyDeclarationContext.class, 0);
        }

        public ExternalDeclarationContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 2;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterExternalDeclaration(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitExternalDeclaration(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitExternalDeclaration(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class FunctionDefinitionContext
    extends ParserRuleContext {
        public FunctionPrototypeContext functionPrototype() {
            return this.getRuleContext(FunctionPrototypeContext.class, 0);
        }

        public CompoundStatementContext compoundStatement() {
            return this.getRuleContext(CompoundStatementContext.class, 0);
        }

        public FunctionDefinitionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 9;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterFunctionDefinition(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitFunctionDefinition(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitFunctionDefinition(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class DeclarationContext
    extends ParserRuleContext {
        public DeclarationContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 12;
        }

        public DeclarationContext() {
        }

        public void copyFrom(DeclarationContext ctx) {
            super.copyFrom(ctx);
        }
    }

    public static class PragmaDirectiveContext
    extends ParserRuleContext {
        public Token stdGL;
        public Token type;
        public Token state;

        public TerminalNode NR() {
            return this.getToken(265, 0);
        }

        public TerminalNode NR_PRAGMA() {
            return this.getToken(276, 0);
        }

        public TerminalNode NR_EOL() {
            return this.getToken(318, 0);
        }

        public TerminalNode NR_LPAREN() {
            return this.getToken(288, 0);
        }

        public TerminalNode NR_RPAREN() {
            return this.getToken(289, 0);
        }

        public TerminalNode NR_IDENTIFIER() {
            return this.getToken(314, 0);
        }

        public TerminalNode NR_PRAGMA_INVARIANT() {
            return this.getToken(279, 0);
        }

        public TerminalNode NR_ALL() {
            return this.getToken(282, 0);
        }

        public TerminalNode NR_STDGL() {
            return this.getToken(290, 0);
        }

        public TerminalNode NR_PRAGMA_DEBUG() {
            return this.getToken(277, 0);
        }

        public TerminalNode NR_PRAGMA_OPTIMIZE() {
            return this.getToken(278, 0);
        }

        public TerminalNode NR_ON() {
            return this.getToken(280, 0);
        }

        public TerminalNode NR_OFF() {
            return this.getToken(281, 0);
        }

        public PragmaDirectiveContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 4;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterPragmaDirective(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitPragmaDirective(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitPragmaDirective(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class ExtensionDirectiveContext
    extends ParserRuleContext {
        public Token extensionName;
        public Token extensionBehavior;

        public TerminalNode NR() {
            return this.getToken(265, 0);
        }

        public TerminalNode NR_EXTENSION() {
            return this.getToken(272, 0);
        }

        public TerminalNode NR_EOL() {
            return this.getToken(318, 0);
        }

        public TerminalNode NR_IDENTIFIER() {
            return this.getToken(314, 0);
        }

        public TerminalNode NR_COLON() {
            return this.getToken(287, 0);
        }

        public TerminalNode NR_REQUIRE() {
            return this.getToken(283, 0);
        }

        public TerminalNode NR_ENABLE() {
            return this.getToken(284, 0);
        }

        public TerminalNode NR_WARN() {
            return this.getToken(285, 0);
        }

        public TerminalNode NR_DISABLE() {
            return this.getToken(286, 0);
        }

        public ExtensionDirectiveContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 5;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterExtensionDirective(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitExtensionDirective(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitExtensionDirective(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class CustomDirectiveContext
    extends ParserRuleContext {
        public Token content;

        public TerminalNode NR() {
            return this.getToken(265, 0);
        }

        public TerminalNode NR_CUSTOM() {
            return this.getToken(274, 0);
        }

        public TerminalNode C_EOL() {
            return this.getToken(326, 0);
        }

        public TerminalNode C_CONTENT() {
            return this.getToken(328, 0);
        }

        public CustomDirectiveContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 6;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterCustomDirective(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitCustomDirective(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitCustomDirective(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class IncludeDirectiveContext
    extends ParserRuleContext {
        public Token content;
        public Token angleStart;

        public TerminalNode NR() {
            return this.getToken(265, 0);
        }

        public TerminalNode NR_INCLUDE() {
            return this.getToken(275, 0);
        }

        public TerminalNode NR_EOL() {
            return this.getToken(318, 0);
        }

        public TerminalNode NR_STRING_START() {
            return this.getToken(311, 0);
        }

        public TerminalNode S_STRING_END() {
            return this.getToken(321, 0);
        }

        public TerminalNode S_STRING_END_ANGLE() {
            return this.getToken(323, 0);
        }

        public TerminalNode NR_STRING_START_ANGLE() {
            return this.getToken(312, 0);
        }

        public TerminalNode S_CONTENT() {
            return this.getToken(320, 0);
        }

        public TerminalNode S_CONTENT_ANGLE() {
            return this.getToken(322, 0);
        }

        public IncludeDirectiveContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 7;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterIncludeDirective(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitIncludeDirective(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitIncludeDirective(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class LayoutDefaultsContext
    extends ParserRuleContext {
        public Token layoutMode;

        public LayoutQualifierContext layoutQualifier() {
            return this.getRuleContext(LayoutQualifierContext.class, 0);
        }

        public TerminalNode SEMICOLON() {
            return this.getToken(243, 0);
        }

        public TerminalNode UNIFORM() {
            return this.getToken(2, 0);
        }

        public TerminalNode IN() {
            return this.getToken(4, 0);
        }

        public TerminalNode OUT() {
            return this.getToken(5, 0);
        }

        public TerminalNode BUFFER() {
            return this.getToken(3, 0);
        }

        public LayoutDefaultsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 8;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterLayoutDefaults(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitLayoutDefaults(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitLayoutDefaults(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class EmptyDeclarationContext
    extends ParserRuleContext {
        public TerminalNode SEMICOLON() {
            return this.getToken(243, 0);
        }

        public EmptyDeclarationContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 3;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterEmptyDeclaration(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitEmptyDeclaration(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitEmptyDeclaration(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class LayoutQualifierContext
    extends ParserRuleContext {
        public LayoutQualifierIdContext layoutQualifierId;
        public List<LayoutQualifierIdContext> layoutQualifiers = new ArrayList<LayoutQualifierIdContext>();

        public TerminalNode LAYOUT() {
            return this.getToken(21, 0);
        }

        public TerminalNode LPAREN() {
            return this.getToken(239, 0);
        }

        public TerminalNode RPAREN() {
            return this.getToken(240, 0);
        }

        public List<LayoutQualifierIdContext> layoutQualifierId() {
            return this.getRuleContexts(LayoutQualifierIdContext.class);
        }

        public LayoutQualifierIdContext layoutQualifierId(int i) {
            return this.getRuleContext(LayoutQualifierIdContext.class, i);
        }

        public List<TerminalNode> COMMA() {
            return this.getTokens(246);
        }

        public TerminalNode COMMA(int i) {
            return this.getToken(246, i);
        }

        public LayoutQualifierContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 21;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterLayoutQualifier(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitLayoutQualifier(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitLayoutQualifier(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class FunctionPrototypeContext
    extends ParserRuleContext {
        public FullySpecifiedTypeContext fullySpecifiedType() {
            return this.getRuleContext(FullySpecifiedTypeContext.class, 0);
        }

        public TerminalNode IDENTIFIER() {
            return this.getToken(266, 0);
        }

        public TerminalNode LPAREN() {
            return this.getToken(239, 0);
        }

        public FunctionParameterListContext functionParameterList() {
            return this.getRuleContext(FunctionParameterListContext.class, 0);
        }

        public TerminalNode RPAREN() {
            return this.getToken(240, 0);
        }

        public List<AttributeContext> attribute() {
            return this.getRuleContexts(AttributeContext.class);
        }

        public AttributeContext attribute(int i) {
            return this.getRuleContext(AttributeContext.class, i);
        }

        public FunctionPrototypeContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 13;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterFunctionPrototype(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitFunctionPrototype(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitFunctionPrototype(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class CompoundStatementContext
    extends ParserRuleContext {
        public TerminalNode LBRACE() {
            return this.getToken(241, 0);
        }

        public TerminalNode RBRACE() {
            return this.getToken(242, 0);
        }

        public List<StatementContext> statement() {
            return this.getRuleContexts(StatementContext.class);
        }

        public StatementContext statement(int i) {
            return this.getRuleContext(StatementContext.class, i);
        }

        public CompoundStatementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 39;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterCompoundStatement(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitCompoundStatement(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitCompoundStatement(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class FiniteExpressionContext
    extends ParserRuleContext {
        public FiniteExpressionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 10;
        }

        public FiniteExpressionContext() {
        }

        public void copyFrom(FiniteExpressionContext ctx) {
            super.copyFrom(ctx);
        }
    }

    public static class ReferenceExpressionContext
    extends FiniteExpressionContext {
        public TerminalNode IDENTIFIER() {
            return this.getToken(266, 0);
        }

        public ReferenceExpressionContext(FiniteExpressionContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterReferenceExpression(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitReferenceExpression(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitReferenceExpression(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class LiteralExpressionContext
    extends FiniteExpressionContext {
        public TerminalNode INT16CONSTANT() {
            return this.getToken(60, 0);
        }

        public TerminalNode UINT16CONSTANT() {
            return this.getToken(59, 0);
        }

        public TerminalNode INT32CONSTANT() {
            return this.getToken(62, 0);
        }

        public TerminalNode UINT32CONSTANT() {
            return this.getToken(61, 0);
        }

        public TerminalNode INT64CONSTANT() {
            return this.getToken(64, 0);
        }

        public TerminalNode UINT64CONSTANT() {
            return this.getToken(63, 0);
        }

        public TerminalNode FLOAT16CONSTANT() {
            return this.getToken(65, 0);
        }

        public TerminalNode FLOAT32CONSTANT() {
            return this.getToken(66, 0);
        }

        public TerminalNode FLOAT64CONSTANT() {
            return this.getToken(67, 0);
        }

        public TerminalNode BOOLCONSTANT() {
            return this.getToken(68, 0);
        }

        public LiteralExpressionContext(FiniteExpressionContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterLiteralExpression(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitLiteralExpression(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitLiteralExpression(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class GroupingExpressionContext
    extends FiniteExpressionContext {
        public ExpressionContext value;

        public TerminalNode LPAREN() {
            return this.getToken(239, 0);
        }

        public TerminalNode RPAREN() {
            return this.getToken(240, 0);
        }

        public ExpressionContext expression() {
            return this.getRuleContext(ExpressionContext.class, 0);
        }

        public GroupingExpressionContext(FiniteExpressionContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterGroupingExpression(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitGroupingExpression(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitGroupingExpression(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class ExpressionContext
    extends ParserRuleContext {
        public FiniteExpressionContext finiteExpression;
        public List<FiniteExpressionContext> items = new ArrayList<FiniteExpressionContext>();

        public List<FiniteExpressionContext> finiteExpression() {
            return this.getRuleContexts(FiniteExpressionContext.class);
        }

        public FiniteExpressionContext finiteExpression(int i) {
            return this.getRuleContext(FiniteExpressionContext.class, i);
        }

        public List<TerminalNode> COMMA() {
            return this.getTokens(246);
        }

        public TerminalNode COMMA(int i) {
            return this.getToken(246, i);
        }

        public ExpressionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 11;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterExpression(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitExpression(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitExpression(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class FunctionCallExpressionContext
    extends FiniteExpressionContext {
        public FiniteExpressionContext finiteExpression;
        public List<FiniteExpressionContext> parameters = new ArrayList<FiniteExpressionContext>();

        public TerminalNode LPAREN() {
            return this.getToken(239, 0);
        }

        public TerminalNode RPAREN() {
            return this.getToken(240, 0);
        }

        public TerminalNode IDENTIFIER() {
            return this.getToken(266, 0);
        }

        public TypeSpecifierContext typeSpecifier() {
            return this.getRuleContext(TypeSpecifierContext.class, 0);
        }

        public TerminalNode VOID() {
            return this.getToken(219, 0);
        }

        public List<FiniteExpressionContext> finiteExpression() {
            return this.getRuleContexts(FiniteExpressionContext.class);
        }

        public FiniteExpressionContext finiteExpression(int i) {
            return this.getRuleContext(FiniteExpressionContext.class, i);
        }

        public List<TerminalNode> COMMA() {
            return this.getTokens(246);
        }

        public TerminalNode COMMA(int i) {
            return this.getToken(246, i);
        }

        public FunctionCallExpressionContext(FiniteExpressionContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterFunctionCallExpression(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitFunctionCallExpression(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitFunctionCallExpression(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class TypeSpecifierContext
    extends ParserRuleContext {
        public TerminalNode IDENTIFIER() {
            return this.getToken(266, 0);
        }

        public BuiltinTypeSpecifierFixedContext builtinTypeSpecifierFixed() {
            return this.getRuleContext(BuiltinTypeSpecifierFixedContext.class, 0);
        }

        public BuiltinTypeSpecifierParseableContext builtinTypeSpecifierParseable() {
            return this.getRuleContext(BuiltinTypeSpecifierParseableContext.class, 0);
        }

        public StructSpecifierContext structSpecifier() {
            return this.getRuleContext(StructSpecifierContext.class, 0);
        }

        public ArraySpecifierContext arraySpecifier() {
            return this.getRuleContext(ArraySpecifierContext.class, 0);
        }

        public TypeSpecifierContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 28;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterTypeSpecifier(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitTypeSpecifier(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitTypeSpecifier(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class PrefixExpressionContext
    extends FiniteExpressionContext {
        public Token op;
        public FiniteExpressionContext operand;

        public FiniteExpressionContext finiteExpression() {
            return this.getRuleContext(FiniteExpressionContext.class, 0);
        }

        public TerminalNode INC_OP() {
            return this.getToken(217, 0);
        }

        public TerminalNode DEC_OP() {
            return this.getToken(218, 0);
        }

        public TerminalNode PLUS_OP() {
            return this.getToken(248, 0);
        }

        public TerminalNode MINUS_OP() {
            return this.getToken(249, 0);
        }

        public TerminalNode LOGICAL_NOT_OP() {
            return this.getToken(250, 0);
        }

        public TerminalNode BITWISE_NEG_OP() {
            return this.getToken(251, 0);
        }

        public PrefixExpressionContext(FiniteExpressionContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterPrefixExpression(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitPrefixExpression(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitPrefixExpression(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class MultiplicativeExpressionContext
    extends FiniteExpressionContext {
        public FiniteExpressionContext left;
        public Token op;
        public FiniteExpressionContext right;

        public List<FiniteExpressionContext> finiteExpression() {
            return this.getRuleContexts(FiniteExpressionContext.class);
        }

        public FiniteExpressionContext finiteExpression(int i) {
            return this.getRuleContext(FiniteExpressionContext.class, i);
        }

        public TerminalNode TIMES_OP() {
            return this.getToken(252, 0);
        }

        public TerminalNode DIV_OP() {
            return this.getToken(253, 0);
        }

        public TerminalNode MOD_OP() {
            return this.getToken(254, 0);
        }

        public MultiplicativeExpressionContext(FiniteExpressionContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterMultiplicativeExpression(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitMultiplicativeExpression(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitMultiplicativeExpression(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class AdditiveExpressionContext
    extends FiniteExpressionContext {
        public FiniteExpressionContext left;
        public Token op;
        public FiniteExpressionContext right;

        public List<FiniteExpressionContext> finiteExpression() {
            return this.getRuleContexts(FiniteExpressionContext.class);
        }

        public FiniteExpressionContext finiteExpression(int i) {
            return this.getRuleContext(FiniteExpressionContext.class, i);
        }

        public TerminalNode PLUS_OP() {
            return this.getToken(248, 0);
        }

        public TerminalNode MINUS_OP() {
            return this.getToken(249, 0);
        }

        public AdditiveExpressionContext(FiniteExpressionContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterAdditiveExpression(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitAdditiveExpression(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitAdditiveExpression(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class ShiftExpressionContext
    extends FiniteExpressionContext {
        public FiniteExpressionContext left;
        public Token op;
        public FiniteExpressionContext right;

        public List<FiniteExpressionContext> finiteExpression() {
            return this.getRuleContexts(FiniteExpressionContext.class);
        }

        public FiniteExpressionContext finiteExpression(int i) {
            return this.getRuleContext(FiniteExpressionContext.class, i);
        }

        public TerminalNode LEFT_OP() {
            return this.getToken(220, 0);
        }

        public TerminalNode RIGHT_OP() {
            return this.getToken(221, 0);
        }

        public ShiftExpressionContext(FiniteExpressionContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterShiftExpression(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitShiftExpression(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitShiftExpression(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class RelationalExpressionContext
    extends FiniteExpressionContext {
        public FiniteExpressionContext left;
        public Token op;
        public FiniteExpressionContext right;

        public List<FiniteExpressionContext> finiteExpression() {
            return this.getRuleContexts(FiniteExpressionContext.class);
        }

        public FiniteExpressionContext finiteExpression(int i) {
            return this.getRuleContext(FiniteExpressionContext.class, i);
        }

        public TerminalNode LT_OP() {
            return this.getToken(255, 0);
        }

        public TerminalNode GT_OP() {
            return this.getToken(256, 0);
        }

        public TerminalNode LE_OP() {
            return this.getToken(222, 0);
        }

        public TerminalNode GE_OP() {
            return this.getToken(223, 0);
        }

        public RelationalExpressionContext(FiniteExpressionContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterRelationalExpression(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitRelationalExpression(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitRelationalExpression(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class EqualityExpressionContext
    extends FiniteExpressionContext {
        public FiniteExpressionContext left;
        public Token op;
        public FiniteExpressionContext right;

        public List<FiniteExpressionContext> finiteExpression() {
            return this.getRuleContexts(FiniteExpressionContext.class);
        }

        public FiniteExpressionContext finiteExpression(int i) {
            return this.getRuleContext(FiniteExpressionContext.class, i);
        }

        public TerminalNode EQ_OP() {
            return this.getToken(224, 0);
        }

        public TerminalNode NE_OP() {
            return this.getToken(225, 0);
        }

        public EqualityExpressionContext(FiniteExpressionContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterEqualityExpression(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitEqualityExpression(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitEqualityExpression(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class BitwiseAndExpressionContext
    extends FiniteExpressionContext {
        public FiniteExpressionContext left;
        public Token op;
        public FiniteExpressionContext right;

        public List<FiniteExpressionContext> finiteExpression() {
            return this.getRuleContexts(FiniteExpressionContext.class);
        }

        public FiniteExpressionContext finiteExpression(int i) {
            return this.getRuleContext(FiniteExpressionContext.class, i);
        }

        public TerminalNode BITWISE_AND_OP() {
            return this.getToken(257, 0);
        }

        public BitwiseAndExpressionContext(FiniteExpressionContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterBitwiseAndExpression(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitBitwiseAndExpression(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitBitwiseAndExpression(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class BitwiseExclusiveOrExpressionContext
    extends FiniteExpressionContext {
        public FiniteExpressionContext left;
        public Token op;
        public FiniteExpressionContext right;

        public List<FiniteExpressionContext> finiteExpression() {
            return this.getRuleContexts(FiniteExpressionContext.class);
        }

        public FiniteExpressionContext finiteExpression(int i) {
            return this.getRuleContext(FiniteExpressionContext.class, i);
        }

        public TerminalNode BITWISE_XOR_OP() {
            return this.getToken(259, 0);
        }

        public BitwiseExclusiveOrExpressionContext(FiniteExpressionContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterBitwiseExclusiveOrExpression(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitBitwiseExclusiveOrExpression(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitBitwiseExclusiveOrExpression(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class BitwiseInclusiveOrExpressionContext
    extends FiniteExpressionContext {
        public FiniteExpressionContext left;
        public Token op;
        public FiniteExpressionContext right;

        public List<FiniteExpressionContext> finiteExpression() {
            return this.getRuleContexts(FiniteExpressionContext.class);
        }

        public FiniteExpressionContext finiteExpression(int i) {
            return this.getRuleContext(FiniteExpressionContext.class, i);
        }

        public TerminalNode BITWISE_OR_OP() {
            return this.getToken(258, 0);
        }

        public BitwiseInclusiveOrExpressionContext(FiniteExpressionContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterBitwiseInclusiveOrExpression(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitBitwiseInclusiveOrExpression(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitBitwiseInclusiveOrExpression(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class LogicalAndExpressionContext
    extends FiniteExpressionContext {
        public FiniteExpressionContext left;
        public Token op;
        public FiniteExpressionContext right;

        public List<FiniteExpressionContext> finiteExpression() {
            return this.getRuleContexts(FiniteExpressionContext.class);
        }

        public FiniteExpressionContext finiteExpression(int i) {
            return this.getRuleContext(FiniteExpressionContext.class, i);
        }

        public TerminalNode LOGICAL_AND_OP() {
            return this.getToken(226, 0);
        }

        public LogicalAndExpressionContext(FiniteExpressionContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterLogicalAndExpression(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitLogicalAndExpression(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitLogicalAndExpression(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class LogicalExclusiveOrExpressionContext
    extends FiniteExpressionContext {
        public FiniteExpressionContext left;
        public Token op;
        public FiniteExpressionContext right;

        public List<FiniteExpressionContext> finiteExpression() {
            return this.getRuleContexts(FiniteExpressionContext.class);
        }

        public FiniteExpressionContext finiteExpression(int i) {
            return this.getRuleContext(FiniteExpressionContext.class, i);
        }

        public TerminalNode LOGICAL_XOR_OP() {
            return this.getToken(227, 0);
        }

        public LogicalExclusiveOrExpressionContext(FiniteExpressionContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterLogicalExclusiveOrExpression(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitLogicalExclusiveOrExpression(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitLogicalExclusiveOrExpression(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class LogicalInclusiveOrExpressionContext
    extends FiniteExpressionContext {
        public FiniteExpressionContext left;
        public Token op;
        public FiniteExpressionContext right;

        public List<FiniteExpressionContext> finiteExpression() {
            return this.getRuleContexts(FiniteExpressionContext.class);
        }

        public FiniteExpressionContext finiteExpression(int i) {
            return this.getRuleContext(FiniteExpressionContext.class, i);
        }

        public TerminalNode LOGICAL_OR_OP() {
            return this.getToken(228, 0);
        }

        public LogicalInclusiveOrExpressionContext(FiniteExpressionContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterLogicalInclusiveOrExpression(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitLogicalInclusiveOrExpression(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitLogicalInclusiveOrExpression(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class ConditionalExpressionContext
    extends FiniteExpressionContext {
        public FiniteExpressionContext condition;
        public FiniteExpressionContext trueAlternative;
        public FiniteExpressionContext falseAlternative;

        public TerminalNode QUERY_OP() {
            return this.getToken(260, 0);
        }

        public TerminalNode COLON() {
            return this.getToken(1, 0);
        }

        public List<FiniteExpressionContext> finiteExpression() {
            return this.getRuleContexts(FiniteExpressionContext.class);
        }

        public FiniteExpressionContext finiteExpression(int i) {
            return this.getRuleContext(FiniteExpressionContext.class, i);
        }

        public ConditionalExpressionContext(FiniteExpressionContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterConditionalExpression(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitConditionalExpression(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitConditionalExpression(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class AssignmentExpressionContext
    extends FiniteExpressionContext {
        public FiniteExpressionContext left;
        public Token op;
        public FiniteExpressionContext right;

        public List<FiniteExpressionContext> finiteExpression() {
            return this.getRuleContexts(FiniteExpressionContext.class);
        }

        public FiniteExpressionContext finiteExpression(int i) {
            return this.getRuleContext(FiniteExpressionContext.class, i);
        }

        public TerminalNode ASSIGN_OP() {
            return this.getToken(261, 0);
        }

        public TerminalNode MUL_ASSIGN() {
            return this.getToken(229, 0);
        }

        public TerminalNode DIV_ASSIGN() {
            return this.getToken(230, 0);
        }

        public TerminalNode MOD_ASSIGN() {
            return this.getToken(231, 0);
        }

        public TerminalNode ADD_ASSIGN() {
            return this.getToken(232, 0);
        }

        public TerminalNode SUB_ASSIGN() {
            return this.getToken(233, 0);
        }

        public TerminalNode LEFT_ASSIGN() {
            return this.getToken(234, 0);
        }

        public TerminalNode RIGHT_ASSIGN() {
            return this.getToken(235, 0);
        }

        public TerminalNode AND_ASSIGN() {
            return this.getToken(236, 0);
        }

        public TerminalNode XOR_ASSIGN() {
            return this.getToken(237, 0);
        }

        public TerminalNode OR_ASSIGN() {
            return this.getToken(238, 0);
        }

        public AssignmentExpressionContext(FiniteExpressionContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterAssignmentExpression(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitAssignmentExpression(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitAssignmentExpression(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class ArrayAccessExpressionContext
    extends FiniteExpressionContext {
        public FiniteExpressionContext left;
        public ExpressionContext right;

        public TerminalNode LBRACKET() {
            return this.getToken(244, 0);
        }

        public TerminalNode RBRACKET() {
            return this.getToken(245, 0);
        }

        public FiniteExpressionContext finiteExpression() {
            return this.getRuleContext(FiniteExpressionContext.class, 0);
        }

        public ExpressionContext expression() {
            return this.getRuleContext(ExpressionContext.class, 0);
        }

        public ArrayAccessExpressionContext(FiniteExpressionContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterArrayAccessExpression(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitArrayAccessExpression(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitArrayAccessExpression(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class LengthAccessExpressionContext
    extends FiniteExpressionContext {
        public FiniteExpressionContext operand;

        public TerminalNode DOT_LENGTH_METHOD_CALL() {
            return this.getToken(22, 0);
        }

        public FiniteExpressionContext finiteExpression() {
            return this.getRuleContext(FiniteExpressionContext.class, 0);
        }

        public LengthAccessExpressionContext(FiniteExpressionContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterLengthAccessExpression(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitLengthAccessExpression(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitLengthAccessExpression(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class MemberAccessExpressionContext
    extends FiniteExpressionContext {
        public FiniteExpressionContext operand;
        public Token member;

        public TerminalNode DOT() {
            return this.getToken(247, 0);
        }

        public FiniteExpressionContext finiteExpression() {
            return this.getRuleContext(FiniteExpressionContext.class, 0);
        }

        public TerminalNode IDENTIFIER() {
            return this.getToken(266, 0);
        }

        public MemberAccessExpressionContext(FiniteExpressionContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterMemberAccessExpression(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitMemberAccessExpression(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitMemberAccessExpression(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class PostfixExpressionContext
    extends FiniteExpressionContext {
        public FiniteExpressionContext operand;
        public Token op;

        public FiniteExpressionContext finiteExpression() {
            return this.getRuleContext(FiniteExpressionContext.class, 0);
        }

        public TerminalNode INC_OP() {
            return this.getToken(217, 0);
        }

        public TerminalNode DEC_OP() {
            return this.getToken(218, 0);
        }

        public PostfixExpressionContext(FiniteExpressionContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterPostfixExpression(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitPostfixExpression(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitPostfixExpression(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class FunctionDeclarationContext
    extends DeclarationContext {
        public FunctionPrototypeContext functionPrototype() {
            return this.getRuleContext(FunctionPrototypeContext.class, 0);
        }

        public TerminalNode SEMICOLON() {
            return this.getToken(243, 0);
        }

        public FunctionDeclarationContext(DeclarationContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterFunctionDeclaration(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitFunctionDeclaration(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitFunctionDeclaration(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class InterfaceBlockDeclarationContext
    extends DeclarationContext {
        public Token blockName;
        public Token variableName;

        public TypeQualifierContext typeQualifier() {
            return this.getRuleContext(TypeQualifierContext.class, 0);
        }

        public StructBodyContext structBody() {
            return this.getRuleContext(StructBodyContext.class, 0);
        }

        public TerminalNode SEMICOLON() {
            return this.getToken(243, 0);
        }

        public List<TerminalNode> IDENTIFIER() {
            return this.getTokens(266);
        }

        public TerminalNode IDENTIFIER(int i) {
            return this.getToken(266, i);
        }

        public ArraySpecifierContext arraySpecifier() {
            return this.getRuleContext(ArraySpecifierContext.class, 0);
        }

        public InterfaceBlockDeclarationContext(DeclarationContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterInterfaceBlockDeclaration(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitInterfaceBlockDeclaration(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitInterfaceBlockDeclaration(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class TypeQualifierContext
    extends ParserRuleContext {
        public List<StorageQualifierContext> storageQualifier() {
            return this.getRuleContexts(StorageQualifierContext.class);
        }

        public StorageQualifierContext storageQualifier(int i) {
            return this.getRuleContext(StorageQualifierContext.class, i);
        }

        public List<LayoutQualifierContext> layoutQualifier() {
            return this.getRuleContexts(LayoutQualifierContext.class);
        }

        public LayoutQualifierContext layoutQualifier(int i) {
            return this.getRuleContext(LayoutQualifierContext.class, i);
        }

        public List<PrecisionQualifierContext> precisionQualifier() {
            return this.getRuleContexts(PrecisionQualifierContext.class);
        }

        public PrecisionQualifierContext precisionQualifier(int i) {
            return this.getRuleContext(PrecisionQualifierContext.class, i);
        }

        public List<InterpolationQualifierContext> interpolationQualifier() {
            return this.getRuleContexts(InterpolationQualifierContext.class);
        }

        public InterpolationQualifierContext interpolationQualifier(int i) {
            return this.getRuleContext(InterpolationQualifierContext.class, i);
        }

        public List<InvariantQualifierContext> invariantQualifier() {
            return this.getRuleContexts(InvariantQualifierContext.class);
        }

        public InvariantQualifierContext invariantQualifier(int i) {
            return this.getRuleContext(InvariantQualifierContext.class, i);
        }

        public List<PreciseQualifierContext> preciseQualifier() {
            return this.getRuleContexts(PreciseQualifierContext.class);
        }

        public PreciseQualifierContext preciseQualifier(int i) {
            return this.getRuleContext(PreciseQualifierContext.class, i);
        }

        public TypeQualifierContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 27;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterTypeQualifier(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitTypeQualifier(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitTypeQualifier(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class StructBodyContext
    extends ParserRuleContext {
        public TerminalNode LBRACE() {
            return this.getToken(241, 0);
        }

        public TerminalNode RBRACE() {
            return this.getToken(242, 0);
        }

        public List<StructMemberContext> structMember() {
            return this.getRuleContexts(StructMemberContext.class);
        }

        public StructMemberContext structMember(int i) {
            return this.getRuleContext(StructMemberContext.class, i);
        }

        public StructBodyContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 34;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterStructBody(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitStructBody(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitStructBody(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class ArraySpecifierContext
    extends ParserRuleContext {
        public List<ArraySpecifierSegmentContext> arraySpecifierSegment() {
            return this.getRuleContexts(ArraySpecifierSegmentContext.class);
        }

        public ArraySpecifierSegmentContext arraySpecifierSegment(int i) {
            return this.getRuleContext(ArraySpecifierSegmentContext.class, i);
        }

        public ArraySpecifierContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 29;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterArraySpecifier(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitArraySpecifier(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitArraySpecifier(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class VariableDeclarationContext
    extends DeclarationContext {
        public Token IDENTIFIER;
        public List<Token> variableNames = new ArrayList<Token>();

        public TypeQualifierContext typeQualifier() {
            return this.getRuleContext(TypeQualifierContext.class, 0);
        }

        public TerminalNode SEMICOLON() {
            return this.getToken(243, 0);
        }

        public List<TerminalNode> IDENTIFIER() {
            return this.getTokens(266);
        }

        public TerminalNode IDENTIFIER(int i) {
            return this.getToken(266, i);
        }

        public List<TerminalNode> COMMA() {
            return this.getTokens(246);
        }

        public TerminalNode COMMA(int i) {
            return this.getToken(246, i);
        }

        public VariableDeclarationContext(DeclarationContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterVariableDeclaration(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitVariableDeclaration(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitVariableDeclaration(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class PrecisionDeclarationContext
    extends DeclarationContext {
        public TerminalNode PRECISION() {
            return this.getToken(10, 0);
        }

        public PrecisionQualifierContext precisionQualifier() {
            return this.getRuleContext(PrecisionQualifierContext.class, 0);
        }

        public TypeSpecifierContext typeSpecifier() {
            return this.getRuleContext(TypeSpecifierContext.class, 0);
        }

        public TerminalNode SEMICOLON() {
            return this.getToken(243, 0);
        }

        public PrecisionDeclarationContext(DeclarationContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterPrecisionDeclaration(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitPrecisionDeclaration(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitPrecisionDeclaration(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class PrecisionQualifierContext
    extends ParserRuleContext {
        public TerminalNode HIGHP() {
            return this.getToken(7, 0);
        }

        public TerminalNode MEDIUMP() {
            return this.getToken(8, 0);
        }

        public TerminalNode LOWP() {
            return this.getToken(9, 0);
        }

        public PrecisionQualifierContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 23;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterPrecisionQualifier(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitPrecisionQualifier(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitPrecisionQualifier(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class TypeAndInitDeclarationContext
    extends DeclarationContext {
        public DeclarationMemberContext declarationMember;
        public List<DeclarationMemberContext> declarationMembers = new ArrayList<DeclarationMemberContext>();

        public FullySpecifiedTypeContext fullySpecifiedType() {
            return this.getRuleContext(FullySpecifiedTypeContext.class, 0);
        }

        public TerminalNode SEMICOLON() {
            return this.getToken(243, 0);
        }

        public List<DeclarationMemberContext> declarationMember() {
            return this.getRuleContexts(DeclarationMemberContext.class);
        }

        public DeclarationMemberContext declarationMember(int i) {
            return this.getRuleContext(DeclarationMemberContext.class, i);
        }

        public List<TerminalNode> COMMA() {
            return this.getTokens(246);
        }

        public TerminalNode COMMA(int i) {
            return this.getToken(246, i);
        }

        public TypeAndInitDeclarationContext(DeclarationContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterTypeAndInitDeclaration(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitTypeAndInitDeclaration(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitTypeAndInitDeclaration(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class FullySpecifiedTypeContext
    extends ParserRuleContext {
        public TypeSpecifierContext typeSpecifier() {
            return this.getRuleContext(TypeSpecifierContext.class, 0);
        }

        public TypeQualifierContext typeQualifier() {
            return this.getRuleContext(TypeQualifierContext.class, 0);
        }

        public FullySpecifiedTypeContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 19;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterFullySpecifiedType(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitFullySpecifiedType(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitFullySpecifiedType(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class DeclarationMemberContext
    extends ParserRuleContext {
        public TerminalNode IDENTIFIER() {
            return this.getToken(266, 0);
        }

        public ArraySpecifierContext arraySpecifier() {
            return this.getRuleContext(ArraySpecifierContext.class, 0);
        }

        public TerminalNode ASSIGN_OP() {
            return this.getToken(261, 0);
        }

        public InitializerContext initializer() {
            return this.getRuleContext(InitializerContext.class, 0);
        }

        public DeclarationMemberContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 18;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterDeclarationMember(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitDeclarationMember(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitDeclarationMember(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class AttributeContext
    extends ParserRuleContext {
        public SingleAttributeContext singleAttribute;
        public List<SingleAttributeContext> attributes = new ArrayList<SingleAttributeContext>();

        public List<TerminalNode> LBRACKET() {
            return this.getTokens(244);
        }

        public TerminalNode LBRACKET(int i) {
            return this.getToken(244, i);
        }

        public List<TerminalNode> RBRACKET() {
            return this.getTokens(245);
        }

        public TerminalNode RBRACKET(int i) {
            return this.getToken(245, i);
        }

        public List<SingleAttributeContext> singleAttribute() {
            return this.getRuleContexts(SingleAttributeContext.class);
        }

        public SingleAttributeContext singleAttribute(int i) {
            return this.getRuleContext(SingleAttributeContext.class, i);
        }

        public List<TerminalNode> COMMA() {
            return this.getTokens(246);
        }

        public TerminalNode COMMA(int i) {
            return this.getToken(246, i);
        }

        public AttributeContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 16;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterAttribute(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitAttribute(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitAttribute(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class FunctionParameterListContext
    extends ParserRuleContext {
        public ParameterDeclarationContext parameterDeclaration;
        public List<ParameterDeclarationContext> parameters = new ArrayList<ParameterDeclarationContext>();

        public List<ParameterDeclarationContext> parameterDeclaration() {
            return this.getRuleContexts(ParameterDeclarationContext.class);
        }

        public ParameterDeclarationContext parameterDeclaration(int i) {
            return this.getRuleContext(ParameterDeclarationContext.class, i);
        }

        public List<TerminalNode> COMMA() {
            return this.getTokens(246);
        }

        public TerminalNode COMMA(int i) {
            return this.getToken(246, i);
        }

        public FunctionParameterListContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 14;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterFunctionParameterList(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitFunctionParameterList(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitFunctionParameterList(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class ParameterDeclarationContext
    extends ParserRuleContext {
        public Token parameterName;

        public FullySpecifiedTypeContext fullySpecifiedType() {
            return this.getRuleContext(FullySpecifiedTypeContext.class, 0);
        }

        public TerminalNode IDENTIFIER() {
            return this.getToken(266, 0);
        }

        public ArraySpecifierContext arraySpecifier() {
            return this.getRuleContext(ArraySpecifierContext.class, 0);
        }

        public ParameterDeclarationContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 15;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterParameterDeclaration(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitParameterDeclaration(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitParameterDeclaration(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class SingleAttributeContext
    extends ParserRuleContext {
        public Token prefix;
        public Token name;
        public ExpressionContext content;

        public List<TerminalNode> IDENTIFIER() {
            return this.getTokens(266);
        }

        public TerminalNode IDENTIFIER(int i) {
            return this.getToken(266, i);
        }

        public List<TerminalNode> COLON() {
            return this.getTokens(1);
        }

        public TerminalNode COLON(int i) {
            return this.getToken(1, i);
        }

        public TerminalNode LPAREN() {
            return this.getToken(239, 0);
        }

        public TerminalNode RPAREN() {
            return this.getToken(240, 0);
        }

        public ExpressionContext expression() {
            return this.getRuleContext(ExpressionContext.class, 0);
        }

        public SingleAttributeContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 17;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterSingleAttribute(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitSingleAttribute(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitSingleAttribute(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class InitializerContext
    extends ParserRuleContext {
        public InitializerContext initializer;
        public List<InitializerContext> initializers = new ArrayList<InitializerContext>();

        public FiniteExpressionContext finiteExpression() {
            return this.getRuleContext(FiniteExpressionContext.class, 0);
        }

        public TerminalNode LBRACE() {
            return this.getToken(241, 0);
        }

        public TerminalNode RBRACE() {
            return this.getToken(242, 0);
        }

        public List<InitializerContext> initializer() {
            return this.getRuleContexts(InitializerContext.class);
        }

        public InitializerContext initializer(int i) {
            return this.getRuleContext(InitializerContext.class, i);
        }

        public List<TerminalNode> COMMA() {
            return this.getTokens(246);
        }

        public TerminalNode COMMA(int i) {
            return this.getToken(246, i);
        }

        public InitializerContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 37;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterInitializer(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitInitializer(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitInitializer(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class StorageQualifierContext
    extends ParserRuleContext {
        public Token IDENTIFIER;
        public List<Token> typeNames = new ArrayList<Token>();

        public TerminalNode CONST() {
            return this.getToken(11, 0);
        }

        public TerminalNode IN() {
            return this.getToken(4, 0);
        }

        public TerminalNode OUT() {
            return this.getToken(5, 0);
        }

        public TerminalNode INOUT() {
            return this.getToken(6, 0);
        }

        public TerminalNode CENTROID() {
            return this.getToken(16, 0);
        }

        public TerminalNode PATCH() {
            return this.getToken(25, 0);
        }

        public TerminalNode SAMPLE() {
            return this.getToken(24, 0);
        }

        public TerminalNode UNIFORM() {
            return this.getToken(2, 0);
        }

        public TerminalNode VARYING() {
            return this.getToken(19, 0);
        }

        public TerminalNode ATTRIBUTE() {
            return this.getToken(17, 0);
        }

        public TerminalNode BUFFER() {
            return this.getToken(3, 0);
        }

        public TerminalNode SHARED() {
            return this.getToken(20, 0);
        }

        public TerminalNode RESTRICT() {
            return this.getToken(27, 0);
        }

        public TerminalNode VOLATILE() {
            return this.getToken(18, 0);
        }

        public TerminalNode COHERENT() {
            return this.getToken(26, 0);
        }

        public TerminalNode READONLY() {
            return this.getToken(28, 0);
        }

        public TerminalNode WRITEONLY() {
            return this.getToken(29, 0);
        }

        public TerminalNode SUBROUTINE() {
            return this.getToken(30, 0);
        }

        public TerminalNode LPAREN() {
            return this.getToken(239, 0);
        }

        public TerminalNode RPAREN() {
            return this.getToken(240, 0);
        }

        public List<TerminalNode> IDENTIFIER() {
            return this.getTokens(266);
        }

        public TerminalNode IDENTIFIER(int i) {
            return this.getToken(266, i);
        }

        public List<TerminalNode> COMMA() {
            return this.getTokens(246);
        }

        public TerminalNode COMMA(int i) {
            return this.getToken(246, i);
        }

        public TerminalNode DEVICECOHERENT() {
            return this.getToken(31, 0);
        }

        public TerminalNode QUEUEFAMILYCOHERENT() {
            return this.getToken(32, 0);
        }

        public TerminalNode WORKGROUPCOHERENT() {
            return this.getToken(33, 0);
        }

        public TerminalNode SUBGROUPCOHERENT() {
            return this.getToken(34, 0);
        }

        public TerminalNode NONPRIVATE() {
            return this.getToken(35, 0);
        }

        public TerminalNode RAY_PAYLOAD_EXT() {
            return this.getToken(36, 0);
        }

        public TerminalNode RAY_PAYLOAD_IN_EXT() {
            return this.getToken(37, 0);
        }

        public TerminalNode HIT_ATTRIBUTE_EXT() {
            return this.getToken(38, 0);
        }

        public TerminalNode CALLABLE_DATA_EXT() {
            return this.getToken(39, 0);
        }

        public TerminalNode CALLABLE_DATA_IN_EXT() {
            return this.getToken(40, 0);
        }

        public StorageQualifierContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 20;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterStorageQualifier(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitStorageQualifier(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitStorageQualifier(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class LayoutQualifierIdContext
    extends ParserRuleContext {
        public LayoutQualifierIdContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 22;
        }

        public LayoutQualifierIdContext() {
        }

        public void copyFrom(LayoutQualifierIdContext ctx) {
            super.copyFrom(ctx);
        }
    }

    public static class NamedLayoutQualifierContext
    extends LayoutQualifierIdContext {
        public TerminalNode IDENTIFIER() {
            return this.getToken(266, 0);
        }

        public TerminalNode ASSIGN_OP() {
            return this.getToken(261, 0);
        }

        public ExpressionContext expression() {
            return this.getRuleContext(ExpressionContext.class, 0);
        }

        public NamedLayoutQualifierContext(LayoutQualifierIdContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterNamedLayoutQualifier(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitNamedLayoutQualifier(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitNamedLayoutQualifier(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class SharedLayoutQualifierContext
    extends LayoutQualifierIdContext {
        public TerminalNode SHARED() {
            return this.getToken(20, 0);
        }

        public SharedLayoutQualifierContext(LayoutQualifierIdContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterSharedLayoutQualifier(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitSharedLayoutQualifier(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitSharedLayoutQualifier(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class InterpolationQualifierContext
    extends ParserRuleContext {
        public TerminalNode SMOOTH() {
            return this.getToken(14, 0);
        }

        public TerminalNode FLAT() {
            return this.getToken(15, 0);
        }

        public TerminalNode NOPERSPECTIVE() {
            return this.getToken(23, 0);
        }

        public InterpolationQualifierContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 24;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterInterpolationQualifier(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitInterpolationQualifier(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitInterpolationQualifier(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class InvariantQualifierContext
    extends ParserRuleContext {
        public TerminalNode INVARIANT() {
            return this.getToken(13, 0);
        }

        public InvariantQualifierContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 25;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterInvariantQualifier(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitInvariantQualifier(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitInvariantQualifier(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class PreciseQualifierContext
    extends ParserRuleContext {
        public TerminalNode PRECISE() {
            return this.getToken(12, 0);
        }

        public PreciseQualifierContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 26;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterPreciseQualifier(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitPreciseQualifier(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitPreciseQualifier(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class BuiltinTypeSpecifierFixedContext
    extends ParserRuleContext {
        public TerminalNode VOID() {
            return this.getToken(219, 0);
        }

        public TerminalNode ATOMIC_UINT() {
            return this.getToken(44, 0);
        }

        public TerminalNode SAMPLER2D() {
            return this.getToken(154, 0);
        }

        public TerminalNode SAMPLER3D() {
            return this.getToken(155, 0);
        }

        public TerminalNode SAMPLERCUBE() {
            return this.getToken(199, 0);
        }

        public TerminalNode SAMPLER2DSHADOW() {
            return this.getToken(158, 0);
        }

        public TerminalNode SAMPLERCUBESHADOW() {
            return this.getToken(197, 0);
        }

        public TerminalNode SAMPLER2DARRAY() {
            return this.getToken(161, 0);
        }

        public TerminalNode SAMPLER2DARRAYSHADOW() {
            return this.getToken(163, 0);
        }

        public TerminalNode SAMPLERCUBEARRAY() {
            return this.getToken(205, 0);
        }

        public TerminalNode SAMPLERCUBEARRAYSHADOW() {
            return this.getToken(198, 0);
        }

        public TerminalNode ISAMPLER2D() {
            return this.getToken(165, 0);
        }

        public TerminalNode ISAMPLER3D() {
            return this.getToken(167, 0);
        }

        public TerminalNode ISAMPLERCUBE() {
            return this.getToken(200, 0);
        }

        public TerminalNode ISAMPLER2DARRAY() {
            return this.getToken(169, 0);
        }

        public TerminalNode ISAMPLERCUBEARRAY() {
            return this.getToken(206, 0);
        }

        public TerminalNode USAMPLER2D() {
            return this.getToken(171, 0);
        }

        public TerminalNode USAMPLER3D() {
            return this.getToken(173, 0);
        }

        public TerminalNode USAMPLERCUBE() {
            return this.getToken(201, 0);
        }

        public TerminalNode USAMPLER2DARRAY() {
            return this.getToken(175, 0);
        }

        public TerminalNode USAMPLERCUBEARRAY() {
            return this.getToken(207, 0);
        }

        public TerminalNode SAMPLER1D() {
            return this.getToken(153, 0);
        }

        public TerminalNode SAMPLER1DSHADOW() {
            return this.getToken(157, 0);
        }

        public TerminalNode SAMPLER1DARRAY() {
            return this.getToken(160, 0);
        }

        public TerminalNode SAMPLER1DARRAYSHADOW() {
            return this.getToken(162, 0);
        }

        public TerminalNode ISAMPLER1D() {
            return this.getToken(164, 0);
        }

        public TerminalNode ISAMPLER1DARRAY() {
            return this.getToken(168, 0);
        }

        public TerminalNode USAMPLER1D() {
            return this.getToken(170, 0);
        }

        public TerminalNode USAMPLER1DARRAY() {
            return this.getToken(174, 0);
        }

        public TerminalNode SAMPLER2DRECT() {
            return this.getToken(156, 0);
        }

        public TerminalNode SAMPLER2DRECTSHADOW() {
            return this.getToken(159, 0);
        }

        public TerminalNode ISAMPLER2DRECT() {
            return this.getToken(166, 0);
        }

        public TerminalNode USAMPLER2DRECT() {
            return this.getToken(172, 0);
        }

        public TerminalNode SAMPLERBUFFER() {
            return this.getToken(202, 0);
        }

        public TerminalNode ISAMPLERBUFFER() {
            return this.getToken(203, 0);
        }

        public TerminalNode USAMPLERBUFFER() {
            return this.getToken(204, 0);
        }

        public TerminalNode SAMPLER2DMS() {
            return this.getToken(176, 0);
        }

        public TerminalNode ISAMPLER2DMS() {
            return this.getToken(177, 0);
        }

        public TerminalNode USAMPLER2DMS() {
            return this.getToken(178, 0);
        }

        public TerminalNode SAMPLER2DMSARRAY() {
            return this.getToken(179, 0);
        }

        public TerminalNode ISAMPLER2DMSARRAY() {
            return this.getToken(180, 0);
        }

        public TerminalNode USAMPLER2DMSARRAY() {
            return this.getToken(181, 0);
        }

        public TerminalNode IMAGE2D() {
            return this.getToken(145, 0);
        }

        public TerminalNode IIMAGE2D() {
            return this.getToken(151, 0);
        }

        public TerminalNode UIMAGE2D() {
            return this.getToken(148, 0);
        }

        public TerminalNode IMAGE3D() {
            return this.getToken(146, 0);
        }

        public TerminalNode IIMAGE3D() {
            return this.getToken(152, 0);
        }

        public TerminalNode UIMAGE3D() {
            return this.getToken(149, 0);
        }

        public TerminalNode IMAGECUBE() {
            return this.getToken(208, 0);
        }

        public TerminalNode IIMAGECUBE() {
            return this.getToken(210, 0);
        }

        public TerminalNode UIMAGECUBE() {
            return this.getToken(209, 0);
        }

        public TerminalNode IMAGEBUFFER() {
            return this.getToken(211, 0);
        }

        public TerminalNode IIMAGEBUFFER() {
            return this.getToken(212, 0);
        }

        public TerminalNode UIMAGEBUFFER() {
            return this.getToken(213, 0);
        }

        public TerminalNode IMAGE1D() {
            return this.getToken(144, 0);
        }

        public TerminalNode IIMAGE1D() {
            return this.getToken(150, 0);
        }

        public TerminalNode UIMAGE1D() {
            return this.getToken(147, 0);
        }

        public TerminalNode IMAGE1DARRAY() {
            return this.getToken(183, 0);
        }

        public TerminalNode IIMAGE1DARRAY() {
            return this.getToken(188, 0);
        }

        public TerminalNode UIMAGE1DARRAY() {
            return this.getToken(193, 0);
        }

        public TerminalNode IMAGE2DRECT() {
            return this.getToken(182, 0);
        }

        public TerminalNode IIMAGE2DRECT() {
            return this.getToken(187, 0);
        }

        public TerminalNode UIMAGE2DRECT() {
            return this.getToken(192, 0);
        }

        public TerminalNode IMAGE2DARRAY() {
            return this.getToken(184, 0);
        }

        public TerminalNode IIMAGE2DARRAY() {
            return this.getToken(189, 0);
        }

        public TerminalNode UIMAGE2DARRAY() {
            return this.getToken(194, 0);
        }

        public TerminalNode IMAGECUBEARRAY() {
            return this.getToken(214, 0);
        }

        public TerminalNode IIMAGECUBEARRAY() {
            return this.getToken(215, 0);
        }

        public TerminalNode UIMAGECUBEARRAY() {
            return this.getToken(216, 0);
        }

        public TerminalNode IMAGE2DMS() {
            return this.getToken(185, 0);
        }

        public TerminalNode IIMAGE2DMS() {
            return this.getToken(190, 0);
        }

        public TerminalNode UIMAGE2DMS() {
            return this.getToken(195, 0);
        }

        public TerminalNode IMAGE2DMSARRAY() {
            return this.getToken(186, 0);
        }

        public TerminalNode IIMAGE2DMSARRAY() {
            return this.getToken(191, 0);
        }

        public TerminalNode UIMAGE2DMSARRAY() {
            return this.getToken(196, 0);
        }

        public TerminalNode ACCELERATION_STRUCTURE_EXT() {
            return this.getToken(43, 0);
        }

        public BuiltinTypeSpecifierFixedContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 32;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterBuiltinTypeSpecifierFixed(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitBuiltinTypeSpecifierFixed(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitBuiltinTypeSpecifierFixed(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class BuiltinTypeSpecifierParseableContext
    extends ParserRuleContext {
        public TerminalNode BOOL() {
            return this.getToken(69, 0);
        }

        public TerminalNode BVEC2() {
            return this.getToken(70, 0);
        }

        public TerminalNode BVEC3() {
            return this.getToken(71, 0);
        }

        public TerminalNode BVEC4() {
            return this.getToken(72, 0);
        }

        public TerminalNode FLOAT16() {
            return this.getToken(105, 0);
        }

        public TerminalNode F16VEC2() {
            return this.getToken(106, 0);
        }

        public TerminalNode F16VEC3() {
            return this.getToken(107, 0);
        }

        public TerminalNode F16VEC4() {
            return this.getToken(108, 0);
        }

        public TerminalNode F16MAT2X2() {
            return this.getToken(109, 0);
        }

        public TerminalNode F16MAT2X3() {
            return this.getToken(110, 0);
        }

        public TerminalNode F16MAT2X4() {
            return this.getToken(111, 0);
        }

        public TerminalNode F16MAT3X2() {
            return this.getToken(112, 0);
        }

        public TerminalNode F16MAT3X3() {
            return this.getToken(113, 0);
        }

        public TerminalNode F16MAT3X4() {
            return this.getToken(114, 0);
        }

        public TerminalNode F16MAT4X2() {
            return this.getToken(115, 0);
        }

        public TerminalNode F16MAT4X3() {
            return this.getToken(116, 0);
        }

        public TerminalNode F16MAT4X4() {
            return this.getToken(117, 0);
        }

        public TerminalNode FLOAT32() {
            return this.getToken(118, 0);
        }

        public TerminalNode F32VEC2() {
            return this.getToken(119, 0);
        }

        public TerminalNode F32VEC3() {
            return this.getToken(120, 0);
        }

        public TerminalNode F32VEC4() {
            return this.getToken(121, 0);
        }

        public TerminalNode F32MAT2X2() {
            return this.getToken(122, 0);
        }

        public TerminalNode F32MAT2X3() {
            return this.getToken(123, 0);
        }

        public TerminalNode F32MAT2X4() {
            return this.getToken(124, 0);
        }

        public TerminalNode F32MAT3X2() {
            return this.getToken(125, 0);
        }

        public TerminalNode F32MAT3X3() {
            return this.getToken(126, 0);
        }

        public TerminalNode F32MAT3X4() {
            return this.getToken(127, 0);
        }

        public TerminalNode F32MAT4X2() {
            return this.getToken(128, 0);
        }

        public TerminalNode F32MAT4X3() {
            return this.getToken(129, 0);
        }

        public TerminalNode F32MAT4X4() {
            return this.getToken(130, 0);
        }

        public TerminalNode FLOAT64() {
            return this.getToken(131, 0);
        }

        public TerminalNode F64VEC2() {
            return this.getToken(132, 0);
        }

        public TerminalNode F64VEC3() {
            return this.getToken(133, 0);
        }

        public TerminalNode F64VEC4() {
            return this.getToken(134, 0);
        }

        public TerminalNode F64MAT2X2() {
            return this.getToken(135, 0);
        }

        public TerminalNode F64MAT2X3() {
            return this.getToken(136, 0);
        }

        public TerminalNode F64MAT2X4() {
            return this.getToken(137, 0);
        }

        public TerminalNode F64MAT3X2() {
            return this.getToken(138, 0);
        }

        public TerminalNode F64MAT3X3() {
            return this.getToken(139, 0);
        }

        public TerminalNode F64MAT3X4() {
            return this.getToken(140, 0);
        }

        public TerminalNode F64MAT4X2() {
            return this.getToken(141, 0);
        }

        public TerminalNode F64MAT4X3() {
            return this.getToken(142, 0);
        }

        public TerminalNode F64MAT4X4() {
            return this.getToken(143, 0);
        }

        public TerminalNode INT8() {
            return this.getToken(73, 0);
        }

        public TerminalNode I8VEC2() {
            return this.getToken(74, 0);
        }

        public TerminalNode I8VEC3() {
            return this.getToken(75, 0);
        }

        public TerminalNode I8VEC4() {
            return this.getToken(76, 0);
        }

        public TerminalNode UINT8() {
            return this.getToken(77, 0);
        }

        public TerminalNode U8VEC2() {
            return this.getToken(78, 0);
        }

        public TerminalNode U8VEC3() {
            return this.getToken(79, 0);
        }

        public TerminalNode U8VEC4() {
            return this.getToken(80, 0);
        }

        public TerminalNode INT16() {
            return this.getToken(81, 0);
        }

        public TerminalNode I16VEC2() {
            return this.getToken(82, 0);
        }

        public TerminalNode I16VEC3() {
            return this.getToken(83, 0);
        }

        public TerminalNode I16VEC4() {
            return this.getToken(84, 0);
        }

        public TerminalNode UINT16() {
            return this.getToken(85, 0);
        }

        public TerminalNode U16VEC2() {
            return this.getToken(86, 0);
        }

        public TerminalNode U16VEC3() {
            return this.getToken(87, 0);
        }

        public TerminalNode U16VEC4() {
            return this.getToken(88, 0);
        }

        public TerminalNode INT32() {
            return this.getToken(89, 0);
        }

        public TerminalNode I32VEC2() {
            return this.getToken(90, 0);
        }

        public TerminalNode I32VEC3() {
            return this.getToken(91, 0);
        }

        public TerminalNode I32VEC4() {
            return this.getToken(92, 0);
        }

        public TerminalNode UINT32() {
            return this.getToken(93, 0);
        }

        public TerminalNode U32VEC2() {
            return this.getToken(94, 0);
        }

        public TerminalNode U32VEC3() {
            return this.getToken(95, 0);
        }

        public TerminalNode U32VEC4() {
            return this.getToken(96, 0);
        }

        public TerminalNode INT64() {
            return this.getToken(97, 0);
        }

        public TerminalNode I64VEC2() {
            return this.getToken(98, 0);
        }

        public TerminalNode I64VEC3() {
            return this.getToken(99, 0);
        }

        public TerminalNode I64VEC4() {
            return this.getToken(100, 0);
        }

        public TerminalNode UINT64() {
            return this.getToken(101, 0);
        }

        public TerminalNode U64VEC2() {
            return this.getToken(102, 0);
        }

        public TerminalNode U64VEC3() {
            return this.getToken(103, 0);
        }

        public TerminalNode U64VEC4() {
            return this.getToken(104, 0);
        }

        public BuiltinTypeSpecifierParseableContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 31;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterBuiltinTypeSpecifierParseable(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitBuiltinTypeSpecifierParseable(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitBuiltinTypeSpecifierParseable(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class StructSpecifierContext
    extends ParserRuleContext {
        public TerminalNode STRUCT() {
            return this.getToken(45, 0);
        }

        public StructBodyContext structBody() {
            return this.getRuleContext(StructBodyContext.class, 0);
        }

        public TerminalNode IDENTIFIER() {
            return this.getToken(266, 0);
        }

        public StructSpecifierContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 33;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterStructSpecifier(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitStructSpecifier(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitStructSpecifier(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class ArraySpecifierSegmentContext
    extends ParserRuleContext {
        public TerminalNode LBRACKET() {
            return this.getToken(244, 0);
        }

        public TerminalNode RBRACKET() {
            return this.getToken(245, 0);
        }

        public ExpressionContext expression() {
            return this.getRuleContext(ExpressionContext.class, 0);
        }

        public ArraySpecifierSegmentContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 30;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterArraySpecifierSegment(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitArraySpecifierSegment(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitArraySpecifierSegment(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class StructMemberContext
    extends ParserRuleContext {
        public StructDeclaratorContext structDeclarator;
        public List<StructDeclaratorContext> structDeclarators = new ArrayList<StructDeclaratorContext>();

        public FullySpecifiedTypeContext fullySpecifiedType() {
            return this.getRuleContext(FullySpecifiedTypeContext.class, 0);
        }

        public TerminalNode SEMICOLON() {
            return this.getToken(243, 0);
        }

        public List<StructDeclaratorContext> structDeclarator() {
            return this.getRuleContexts(StructDeclaratorContext.class);
        }

        public StructDeclaratorContext structDeclarator(int i) {
            return this.getRuleContext(StructDeclaratorContext.class, i);
        }

        public List<TerminalNode> COMMA() {
            return this.getTokens(246);
        }

        public TerminalNode COMMA(int i) {
            return this.getToken(246, i);
        }

        public StructMemberContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 35;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterStructMember(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitStructMember(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitStructMember(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class StructDeclaratorContext
    extends ParserRuleContext {
        public TerminalNode IDENTIFIER() {
            return this.getToken(266, 0);
        }

        public ArraySpecifierContext arraySpecifier() {
            return this.getRuleContext(ArraySpecifierContext.class, 0);
        }

        public StructDeclaratorContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 36;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterStructDeclarator(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitStructDeclarator(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitStructDeclarator(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class StatementContext
    extends ParserRuleContext {
        public CompoundStatementContext compoundStatement() {
            return this.getRuleContext(CompoundStatementContext.class, 0);
        }

        public DeclarationStatementContext declarationStatement() {
            return this.getRuleContext(DeclarationStatementContext.class, 0);
        }

        public ExpressionStatementContext expressionStatement() {
            return this.getRuleContext(ExpressionStatementContext.class, 0);
        }

        public EmptyStatementContext emptyStatement() {
            return this.getRuleContext(EmptyStatementContext.class, 0);
        }

        public SelectionStatementContext selectionStatement() {
            return this.getRuleContext(SelectionStatementContext.class, 0);
        }

        public SwitchStatementContext switchStatement() {
            return this.getRuleContext(SwitchStatementContext.class, 0);
        }

        public CaseLabelContext caseLabel() {
            return this.getRuleContext(CaseLabelContext.class, 0);
        }

        public ForStatementContext forStatement() {
            return this.getRuleContext(ForStatementContext.class, 0);
        }

        public WhileStatementContext whileStatement() {
            return this.getRuleContext(WhileStatementContext.class, 0);
        }

        public DoWhileStatementContext doWhileStatement() {
            return this.getRuleContext(DoWhileStatementContext.class, 0);
        }

        public JumpStatementContext jumpStatement() {
            return this.getRuleContext(JumpStatementContext.class, 0);
        }

        public DemoteStatementContext demoteStatement() {
            return this.getRuleContext(DemoteStatementContext.class, 0);
        }

        public StatementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 38;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterStatement(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitStatement(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitStatement(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class DeclarationStatementContext
    extends ParserRuleContext {
        public DeclarationContext declaration() {
            return this.getRuleContext(DeclarationContext.class, 0);
        }

        public DeclarationStatementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 40;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterDeclarationStatement(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitDeclarationStatement(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitDeclarationStatement(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class ExpressionStatementContext
    extends ParserRuleContext {
        public ExpressionContext expression() {
            return this.getRuleContext(ExpressionContext.class, 0);
        }

        public TerminalNode SEMICOLON() {
            return this.getToken(243, 0);
        }

        public ExpressionStatementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 41;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterExpressionStatement(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitExpressionStatement(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitExpressionStatement(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class EmptyStatementContext
    extends ParserRuleContext {
        public TerminalNode SEMICOLON() {
            return this.getToken(243, 0);
        }

        public EmptyStatementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 42;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterEmptyStatement(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitEmptyStatement(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitEmptyStatement(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class SelectionStatementContext
    extends ParserRuleContext {
        public ExpressionContext condition;
        public StatementContext ifTrue;
        public StatementContext ifFalse;

        public TerminalNode IF() {
            return this.getToken(46, 0);
        }

        public TerminalNode LPAREN() {
            return this.getToken(239, 0);
        }

        public TerminalNode RPAREN() {
            return this.getToken(240, 0);
        }

        public ExpressionContext expression() {
            return this.getRuleContext(ExpressionContext.class, 0);
        }

        public List<StatementContext> statement() {
            return this.getRuleContexts(StatementContext.class);
        }

        public StatementContext statement(int i) {
            return this.getRuleContext(StatementContext.class, i);
        }

        public AttributeContext attribute() {
            return this.getRuleContext(AttributeContext.class, 0);
        }

        public TerminalNode ELSE() {
            return this.getToken(47, 0);
        }

        public SelectionStatementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 43;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterSelectionStatement(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitSelectionStatement(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitSelectionStatement(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class SwitchStatementContext
    extends ParserRuleContext {
        public ExpressionContext condition;

        public TerminalNode SWITCH() {
            return this.getToken(48, 0);
        }

        public TerminalNode LPAREN() {
            return this.getToken(239, 0);
        }

        public TerminalNode RPAREN() {
            return this.getToken(240, 0);
        }

        public CompoundStatementContext compoundStatement() {
            return this.getRuleContext(CompoundStatementContext.class, 0);
        }

        public ExpressionContext expression() {
            return this.getRuleContext(ExpressionContext.class, 0);
        }

        public AttributeContext attribute() {
            return this.getRuleContext(AttributeContext.class, 0);
        }

        public SwitchStatementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 45;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterSwitchStatement(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitSwitchStatement(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitSwitchStatement(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class CaseLabelContext
    extends ParserRuleContext {
        public CaseLabelContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 46;
        }

        public CaseLabelContext() {
        }

        public void copyFrom(CaseLabelContext ctx) {
            super.copyFrom(ctx);
        }
    }

    public static class ForStatementContext
    extends ParserRuleContext {
        public ExpressionContext condition;
        public IterationConditionContext initCondition;
        public ExpressionContext incrementer;
        public StatementContext loopBody;

        public TerminalNode FOR() {
            return this.getToken(53, 0);
        }

        public TerminalNode LPAREN() {
            return this.getToken(239, 0);
        }

        public TerminalNode SEMICOLON() {
            return this.getToken(243, 0);
        }

        public TerminalNode RPAREN() {
            return this.getToken(240, 0);
        }

        public StatementContext statement() {
            return this.getRuleContext(StatementContext.class, 0);
        }

        public EmptyStatementContext emptyStatement() {
            return this.getRuleContext(EmptyStatementContext.class, 0);
        }

        public ExpressionStatementContext expressionStatement() {
            return this.getRuleContext(ExpressionStatementContext.class, 0);
        }

        public DeclarationStatementContext declarationStatement() {
            return this.getRuleContext(DeclarationStatementContext.class, 0);
        }

        public AttributeContext attribute() {
            return this.getRuleContext(AttributeContext.class, 0);
        }

        public List<ExpressionContext> expression() {
            return this.getRuleContexts(ExpressionContext.class);
        }

        public ExpressionContext expression(int i) {
            return this.getRuleContext(ExpressionContext.class, i);
        }

        public IterationConditionContext iterationCondition() {
            return this.getRuleContext(IterationConditionContext.class, 0);
        }

        public ForStatementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 49;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterForStatement(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitForStatement(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitForStatement(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class WhileStatementContext
    extends ParserRuleContext {
        public ExpressionContext condition;
        public IterationConditionContext initCondition;
        public StatementContext loopBody;

        public TerminalNode WHILE() {
            return this.getToken(51, 0);
        }

        public TerminalNode LPAREN() {
            return this.getToken(239, 0);
        }

        public TerminalNode RPAREN() {
            return this.getToken(240, 0);
        }

        public StatementContext statement() {
            return this.getRuleContext(StatementContext.class, 0);
        }

        public AttributeContext attribute() {
            return this.getRuleContext(AttributeContext.class, 0);
        }

        public ExpressionContext expression() {
            return this.getRuleContext(ExpressionContext.class, 0);
        }

        public IterationConditionContext iterationCondition() {
            return this.getRuleContext(IterationConditionContext.class, 0);
        }

        public WhileStatementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 47;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterWhileStatement(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitWhileStatement(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitWhileStatement(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class DoWhileStatementContext
    extends ParserRuleContext {
        public StatementContext loopBody;
        public ExpressionContext condition;

        public TerminalNode DO() {
            return this.getToken(52, 0);
        }

        public TerminalNode WHILE() {
            return this.getToken(51, 0);
        }

        public TerminalNode LPAREN() {
            return this.getToken(239, 0);
        }

        public TerminalNode RPAREN() {
            return this.getToken(240, 0);
        }

        public TerminalNode SEMICOLON() {
            return this.getToken(243, 0);
        }

        public StatementContext statement() {
            return this.getRuleContext(StatementContext.class, 0);
        }

        public ExpressionContext expression() {
            return this.getRuleContext(ExpressionContext.class, 0);
        }

        public AttributeContext attribute() {
            return this.getRuleContext(AttributeContext.class, 0);
        }

        public DoWhileStatementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 48;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterDoWhileStatement(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitDoWhileStatement(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitDoWhileStatement(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class JumpStatementContext
    extends ParserRuleContext {
        public JumpStatementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 50;
        }

        public JumpStatementContext() {
        }

        public void copyFrom(JumpStatementContext ctx) {
            super.copyFrom(ctx);
        }
    }

    public static class DemoteStatementContext
    extends ParserRuleContext {
        public TerminalNode DEMOTE() {
            return this.getToken(58, 0);
        }

        public TerminalNode SEMICOLON() {
            return this.getToken(243, 0);
        }

        public DemoteStatementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 51;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterDemoteStatement(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitDemoteStatement(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitDemoteStatement(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class IterationConditionContext
    extends ParserRuleContext {
        public Token name;

        public FullySpecifiedTypeContext fullySpecifiedType() {
            return this.getRuleContext(FullySpecifiedTypeContext.class, 0);
        }

        public TerminalNode ASSIGN_OP() {
            return this.getToken(261, 0);
        }

        public InitializerContext initializer() {
            return this.getRuleContext(InitializerContext.class, 0);
        }

        public TerminalNode IDENTIFIER() {
            return this.getToken(266, 0);
        }

        public IterationConditionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return 44;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterIterationCondition(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitIterationCondition(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitIterationCondition(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class ValuedCaseLabelContext
    extends CaseLabelContext {
        public TerminalNode CASE() {
            return this.getToken(49, 0);
        }

        public ExpressionContext expression() {
            return this.getRuleContext(ExpressionContext.class, 0);
        }

        public TerminalNode COLON() {
            return this.getToken(1, 0);
        }

        public ValuedCaseLabelContext(CaseLabelContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterValuedCaseLabel(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitValuedCaseLabel(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitValuedCaseLabel(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class DefaultCaseLabelContext
    extends CaseLabelContext {
        public TerminalNode DEFAULT() {
            return this.getToken(50, 0);
        }

        public TerminalNode COLON() {
            return this.getToken(1, 0);
        }

        public DefaultCaseLabelContext(CaseLabelContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterDefaultCaseLabel(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitDefaultCaseLabel(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitDefaultCaseLabel(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class ContinueStatementContext
    extends JumpStatementContext {
        public TerminalNode CONTINUE() {
            return this.getToken(54, 0);
        }

        public TerminalNode SEMICOLON() {
            return this.getToken(243, 0);
        }

        public ContinueStatementContext(JumpStatementContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterContinueStatement(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitContinueStatement(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitContinueStatement(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class BreakStatementContext
    extends JumpStatementContext {
        public TerminalNode BREAK() {
            return this.getToken(55, 0);
        }

        public TerminalNode SEMICOLON() {
            return this.getToken(243, 0);
        }

        public BreakStatementContext(JumpStatementContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterBreakStatement(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitBreakStatement(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitBreakStatement(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class ReturnStatementContext
    extends JumpStatementContext {
        public TerminalNode RETURN() {
            return this.getToken(56, 0);
        }

        public TerminalNode SEMICOLON() {
            return this.getToken(243, 0);
        }

        public ExpressionContext expression() {
            return this.getRuleContext(ExpressionContext.class, 0);
        }

        public ReturnStatementContext(JumpStatementContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterReturnStatement(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitReturnStatement(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitReturnStatement(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class DiscardStatementContext
    extends JumpStatementContext {
        public TerminalNode DISCARD() {
            return this.getToken(57, 0);
        }

        public TerminalNode SEMICOLON() {
            return this.getToken(243, 0);
        }

        public DiscardStatementContext(JumpStatementContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterDiscardStatement(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitDiscardStatement(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitDiscardStatement(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class IgnoreIntersectionStatementContext
    extends JumpStatementContext {
        public TerminalNode IGNORE_INTERSECTION_EXT() {
            return this.getToken(41, 0);
        }

        public TerminalNode SEMICOLON() {
            return this.getToken(243, 0);
        }

        public IgnoreIntersectionStatementContext(JumpStatementContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterIgnoreIntersectionStatement(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitIgnoreIntersectionStatement(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitIgnoreIntersectionStatement(this);
            }
            return visitor.visitChildren(this);
        }
    }

    public static class TerminateRayStatementContext
    extends JumpStatementContext {
        public TerminalNode TERMINATE_RAY_EXT() {
            return this.getToken(42, 0);
        }

        public TerminalNode SEMICOLON() {
            return this.getToken(243, 0);
        }

        public TerminateRayStatementContext(JumpStatementContext ctx) {
            this.copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).enterTerminateRayStatement(this);
            }
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GLSLParserListener) {
                ((GLSLParserListener)listener).exitTerminateRayStatement(this);
            }
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GLSLParserVisitor) {
                return ((GLSLParserVisitor)visitor).visitTerminateRayStatement(this);
            }
            return visitor.visitChildren(this);
        }
    }
}

