import * as yup from 'yup';

export const documentProcessSchema = yup.object().shape({
  names: yup
    .string()
    .required('Los nombres son requeridos')
    .max(100, 'Máximo 100 caracteres')
    .matches(/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/, 'Solo se permiten letras'),

  lastNames: yup
    .string()
    .required('Los apellidos son requeridos')
    .max(100, 'Máximo 100 caracteres')
    .matches(/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/, 'Solo se permiten letras'),

  identity: yup
    .string()
    .required('La identificación es requerida')
    .max(20, 'Máximo 20 caracteres')
    .matches(/^[0-9]+$/, 'Solo se permiten números'),

  date: yup
    .date()
    .required('La fecha es requerida')
    .max(new Date(), 'La fecha no puede ser futura'),

  conduct: yup
    .string()
    .max(255, 'Máximo 255 caracteres'),

  radicado: yup
    .number()
    .typeError('Debe ser un número')
    .positive('Debe ser positivo')
    .integer('Debe ser un número entero')
    .nullable()
    .transform((value, originalValue) => originalValue === '' ? null : value),

  fiscal: yup
    .string()
    .max(100, 'Máximo 100 caracteres'),

  typeAudience: yup
    .string()
    .max(50, 'Máximo 50 caracteres')
    .nullable(),

  fact: yup
    .string()
    .nullable(),

  juzgado: yup
    .string()
    .max(100, 'Máximo 100 caracteres')
    .nullable(),

  state: yup
    .boolean()
    .required()
});

export const audienceTypes = [
  { value: 'IMPUTACION', label: 'Imputación de cargos' },
  { value: 'CONTROL_GARANTIAS', label: 'Control de garantías' },
  { value: 'PREPARATORIA', label: 'Audiencia preparatoria' },
  { value: 'JUICIO_ORAL', label: 'Juicio oral' },
  { value: 'CONCILIACION', label: 'Conciliación' },
  { value: 'APELACION', label: 'Apelación' },
  { value: 'OTRO', label: 'Otro' }
];