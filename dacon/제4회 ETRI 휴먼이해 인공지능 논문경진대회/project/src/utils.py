import os
import glob
import pandas as pd

def load_lifelog_data(base_path='data/ETRI/ETRI_lifelog_dataset/ch2025_data_items'):
    parquet_files = glob.glob(os.path.join(base_path, 'ch2025_*.parquet'))
    lifelog_data = {}
    for file_path in parquet_files:
        key = os.path.basename(file_path).replace('.parquet', '').replace('ch2025_', '')
        df = pd.read_parquet(file_path)
        lifelog_data[key] = df
    return lifelog_data


def describe_and_nulls(df):
    summary = df.describe(include='all')
    nulls = df.isnull().mean().to_frame(name='null_ratio')
    return summary, nulls
