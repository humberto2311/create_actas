import * as yup from 'yup';

export const audienceTypes = [
  { value: 'IMPUTACION', label: 'Audiencia de Imputación' },
  { value: 'CONTROL_GARANTIAS', label: 'Audiencia de Control de Garantías' },
  { value: 'FORMULACION_ACUSACION', label: 'Audiencia de Formulación de Acusación' },
  { value: 'PREPARATORIA', label: 'Audiencia Preparatoria' },
  { value: 'JUICIO_ORAL', label: 'Juicio Oral' },
  { value: 'INDIVIDUALIZACION_PENA', label: 'Individualización de Pena y Sentencia' }
];

export const nacionOptions = [
  { value: 'COLOMBIANA', label: 'Colombiana' },
  { value: 'VENEZOLANA', label: 'Venezolana' },
  { value: 'ECUATORIANA', label: 'Ecuatoriana' },
  { value: 'PERUANA', label: 'Peruana' },
  { value: 'BRASILEÑA', label: 'Brasileña' },
  { value: 'ARGENTINA', label: 'Argentina' },
  { value: 'CHILENA', label: 'Chilena' },
  { value: 'MEXICANA', label: 'Mexicana' },
  { value: 'PANAMEÑA', label: 'Panameña' },
  { value: 'COSTARRICENSE', label: 'Costarricense' },
  { value: 'OTRA', label: 'Otra' }
];

export const documentProcessSchema = yup.object().shape({
  names: yup.string()
    .required('Los nombres son obligatorios')
    .max(100, 'Máximo 100 caracteres'),
  
  lastNames: yup.string()
    .required('Los apellidos son obligatorios')
    .max(100, 'Máximo 100 caracteres'),
  
  identity: yup.string()
    .required('La identificación es obligatoria')
    .max(20, 'Máximo 20 caracteres')
    .matches(/^[0-9]+$/, 'Solo se permiten números'),
  
  nacion: yup.string()
    .required('La nacionalidad es obligatoria')
    .max(20, 'Máximo 20 caracteres'),
  
  date: yup.date()
    .required('La fecha es obligatoria')
    .max(new Date(), 'La fecha no puede ser futura'),
  
  captura: yup.date()
    .required('La fecha de captura es obligatoria')
    .max(new Date(), 'La fecha de captura no puede ser futura'),
  
  conduct: yup.string()
    .max(255, 'Máximo 255 caracteres'),
  
  radicado: yup.string()
    .max(30, 'Máximo 30 caracteres')
    .matches(/^[0-9]*$/, 'Solo se permiten números'),
  
  fiscal: yup.string()
    .max(100, 'Máximo 100 caracteres'),
  
  typeAudience: yup.string()
    .max(50, 'Máximo 50 caracteres'),
  
  fact: yup.string(),
  
  juzgado: yup.string()
    .max(100, 'Máximo 100 caracteres'),
  
  state: yup.boolean()
    .required('El estado es obligatorio')
});