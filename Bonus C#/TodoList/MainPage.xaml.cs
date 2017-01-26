using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Runtime.InteropServices.WindowsRuntime;
using Windows.Foundation;
using Windows.Foundation.Collections;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Controls.Primitives;
using Windows.UI.Xaml.Data;
using Windows.UI.Xaml.Input;
using Windows.UI.Xaml.Media;
using Windows.UI.Xaml.Navigation;
using Windows.Data.Json;
using System.Threading.Tasks;
using Windows.UI.Core;

namespace TodoList {
	public sealed partial class MainPage : Page {
		JsonArray _data = null;
		Windows.Storage.StorageFolder storageFolder = Windows.Storage.ApplicationData.Current.LocalFolder;
		Windows.Storage.StorageFile _datafile = null;
		CoreDispatcher dispatcher = CoreWindow.GetForCurrentThread().Dispatcher;

		private async void InitData() {
			try {
				_datafile = await storageFolder.GetFileAsync("persistence.json");
			} catch { };
			if (_datafile != null) {
				string sData = await Windows.Storage.FileIO.ReadTextAsync(_datafile);
				_data = JsonArray.Parse(sData);
				await Dispatcher.RunAsync(CoreDispatcherPriority.Normal, () => { refreshList(); });
			} else {
				_datafile = await storageFolder.CreateFileAsync("persistence.json");
				_data = new JsonArray();
			}
		}

		public MainPage() {
			this.InitializeComponent();
			DescRichText.Background = new SolidColorBrush(Windows.UI.Color.FromArgb(0, 0, 0, 0));
			List.SelectionChanged += List_SelectionChanged;
			SaveBtn.Click += SaveBtn_Click;
			DeletBtn.Click += DeletBtn_Click;
			Task t = new Task(InitData);
			t.Start();
		}

		private void DeletBtn_Click(object sender, RoutedEventArgs e) {
			_data.RemoveAt(List.SelectedIndex);
			int current = List.SelectedIndex;
			refreshList();
			if (_data.Count < 1) {
				List.SelectedIndex = -1;
			} else {
				List.SelectedIndex = (current < _data.Count) ? current : current - 1;
			}
			Save();
		}

		private void refreshView() {
			if (List.SelectedIndex >= 0) {
				ViewNote.Visibility = Visibility.Visible;
				JsonObject obj = _data.GetObjectAt((uint)List.SelectedIndex);
				DescRichText.Document.SetText(Windows.UI.Text.TextSetOptions.None, obj.GetNamedString("Desc"));
				TitleText.Text = obj.GetNamedString("Title");
			} else {
				ViewNote.Visibility = Visibility.Collapsed;
			}
		}

		private void SaveBtn_Click(object sender, RoutedEventArgs e) {
			int current = List.SelectedIndex;
			JsonObject obj = _data.GetObjectAt((uint)List.SelectedIndex);
			string title = TitleText.Text;
			string desc = null;
			DescRichText.Document.GetText(Windows.UI.Text.TextGetOptions.None, out desc);
			obj.SetNamedValue("Title", JsonValue.CreateStringValue(title));
			obj.SetNamedValue("Desc", JsonValue.CreateStringValue(desc));
			_data[List.SelectedIndex] = obj;
			Save();
			refreshList();
			List.SelectedIndex = current;
		}

		private void List_SelectionChanged(object sender, SelectionChangedEventArgs e) {
			refreshView();
		}

		private void refreshList() {
			List.Items.Clear();
			for (uint i = 0; i < _data.Count; i++) {
				JsonObject obj = _data.GetObjectAt(i);
				List.Items.Add(obj.GetNamedString("Title"));
			}
		}

		private async void SaveFile() {
			await Windows.Storage.FileIO.WriteTextAsync(_datafile, _data.ToString());
		}

		private void Save() {
			Task t = new Task(SaveFile);
			t.Start();
		}

		private void AddBtn_Click(object sender, RoutedEventArgs e) {
			JsonObject obj = new JsonObject();
			obj.SetNamedValue("Title", JsonValue.CreateStringValue("New Task"));
			obj.SetNamedValue("Desc", JsonValue.CreateStringValue(""));
			_data.Add(obj);
			refreshList();
			List.SelectedIndex = _data.Count - 1;
			Save();
		}
	}
}
